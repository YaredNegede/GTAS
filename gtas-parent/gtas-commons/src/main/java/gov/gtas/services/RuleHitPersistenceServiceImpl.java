/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class RuleHitPersistenceServiceImpl implements RuleHitPersistenceService {

	private static final Logger logger = LoggerFactory.getLogger(RuleHitPersistenceServiceImpl.class);

	private final PassengerService passengerService;

	private final HitDetailRepository hitDetailRepository;

	private final HitsSummaryRepository hitsSummaryRepository;

	private final FlightHitsWatchlistRepository flightHitsWatchlistRepository;

	private final FlightHitsRuleRepository flightHitsRuleRepository;

	private final FlightFuzzyHitsRepository flightFuzzyHitsRepository;

	private final FlightGraphHitsRepository flightGraphHitsRepository;

	public RuleHitPersistenceServiceImpl(PassengerService passengerService, HitDetailRepository hitDetailRepository,
			HitsSummaryRepository hitsSummaryRepository, FlightHitsWatchlistRepository flightHitsWatchlistRepository,
			FlightHitsRuleRepository flightHitsRuleRepository, FlightFuzzyHitsRepository flightFuzzyHitsRepository,
			FlightGraphHitsRepository flightGraphHitsRepository) {
		this.passengerService = passengerService;
		this.hitDetailRepository = hitDetailRepository;
		this.hitsSummaryRepository = hitsSummaryRepository;
		this.flightHitsWatchlistRepository = flightHitsWatchlistRepository;
		this.flightHitsRuleRepository = flightHitsRuleRepository;
		this.flightFuzzyHitsRepository = flightFuzzyHitsRepository;
		this.flightGraphHitsRepository = flightGraphHitsRepository;
	}

	@Transactional
	public void persistToDatabase(@NonNull Set<HitDetail> hitDetailSet) {
		try {
			Objects.requireNonNull(hitDetailSet);
			if (!hitDetailSet.isEmpty()) {
				Map<Long, Set<HitDetail>> hitDetailMappedToPassengerId = new HashMap<>();
				for (HitDetail hitDetail : hitDetailSet) {
					Long passengerId = hitDetail.getPassengerId();
					if (hitDetailMappedToPassengerId.containsKey(passengerId)) {
						hitDetailMappedToPassengerId.get(passengerId).add(hitDetail);
					} else {
						Set<HitDetail> hdMapSet = new HashSet<>();
						hdMapSet.add(hitDetail);
						hitDetailMappedToPassengerId.put(passengerId, hdMapSet);
					}
				}
				Set<Long> passengerIds = hitDetailSet.stream().filter(hd -> hd.getPassengerId() != null)
						.map(HitDetail::getPassengerId).collect(Collectors.toSet());
				Set<Passenger> passengersWithHitDetails = passengerService.getPassengersWithHitDetails(passengerIds);
				int newDetails = 0;
				int existingDetails = 0;
				Set<HitDetail> hitDetailsToPersist = new HashSet<>();
				Set<Long> flightIds = passengersWithHitDetails.stream().map(Passenger::getFlight).map(Flight::getId)
						.collect(Collectors.toSet());
				Set<HitsSummary> updatedHitsSummaries = new HashSet<>();
				int ruleHits = 0;
				int wlHits = 0;
				int graphHits = 0;
				int pwlHits = 0;
				int manualHits = 0;
				for (Passenger passenger : passengersWithHitDetails) {
					Set<HitDetail> passengerHitDetails = passenger.getHitDetails();
					Set<HitDetail> ruleEngineHitDetails = hitDetailMappedToPassengerId.get(passenger.getId());
					for (HitDetail ruleEngineDetail : ruleEngineHitDetails) {
						if (passengerHitDetails.contains(ruleEngineDetail)) {
							existingDetails++;
						} else {
							HitsSummary hitsSummary = passenger.getHits();
							if (hitsSummary == null) {
								hitsSummary = new HitsSummary();
								hitsSummary.setPassenger(passenger);
								hitsSummary.setPaxId(passenger.getId());
								passenger.setHits(hitsSummary);
							}
							hitsSummary.setUpdated(true);
							hitsSummary.setFlightId(passenger.getFlight().getId());
							hitsSummary.setFlight(passenger.getFlight());
							switch (ruleEngineDetail.getHitEnum()) {
							case WATCHLIST:
							case WATCHLIST_DOCUMENT:
							case WATCHLIST_PASSENGER:
								hitsSummary.setWatchListHitCount(hitsSummary.getWatchListHitCount() + 1);
								wlHits++;
								break;
							case USER_DEFINED_RULE:
								hitsSummary.setRuleHitCount(hitsSummary.getRuleHitCount() + 1);
								ruleHits++;
								break;
							case GRAPH_HIT:
								hitsSummary.setGraphHitCount(hitsSummary.getGraphHitCount() + 1);
								graphHits++;
								break;
							case PARTIAL_WATCHLIST:
								hitsSummary.setPartialHitCount(hitsSummary.getPartialHitCount() + 1);
								pwlHits++;
								break;
							case MANUAL_HIT:
								hitsSummary.setManualHitCount(hitsSummary.getManualHitCount() + 1);
								manualHits++;
								break;
							default:
								logger.warn("UNIMPLEMENTED FIELD - COUNT NOT SAVED - " + ruleEngineDetail.getHitEnum());
							}
							newDetails++;
							hitDetailsToPersist.add(ruleEngineDetail);
							updatedHitsSummaries.add(hitsSummary);
						}
					}
				}
				if (!hitDetailsToPersist.isEmpty()) {
					hitDetailRepository.saveAll(hitDetailsToPersist);
				}
				if (!updatedHitsSummaries.isEmpty()) {
					hitsSummaryRepository.saveAll(updatedHitsSummaries);
					updateFlightHitCounts(flightIds);
				}
				logger.debug("Processed... rule hits: " + ruleHits + " wlHits: " + wlHits + " graphHits" + graphHits
						+ " partial hits: " + pwlHits + " manual hits: " + manualHits);
				logger.info("Persisted " + newDetails + " new passenger hit details, ignored " + existingDetails
						+ " existing passenger hit details.");
			}
		} catch (Exception e) {
			logger.warn("UNABLE TO CREATE NEW HIT DETAILS. FAILURE! ", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.gtas.svc.TargetingService#updateFlightHitCounts(java.util.Set)
	 */
	@Override
	public void updateFlightHitCounts(Set<Long> flights) {
		logger.debug("Entering updateFlightHitCounts().");
		if (CollectionUtils.isEmpty(flights)) {
			logger.debug("no flight");
			return;
		}
		logger.debug("update rule hit count on flights.");
		Set<FlightHitsRule> flightHitsRules = new HashSet<>();
		Set<FlightHitsWatchlist> flightHitsWatchlists = new HashSet<>();
		Set<FlightHitsGraph> flightHitsGraphs = new HashSet<>();
		Set<FlightHitsFuzzy> flightHitsFuzzies = new HashSet<>();
		for (Long flightId : flights) {
			Integer ruleHits = hitsSummaryRepository.ruleHitCount(flightId);
			FlightHitsRule ruleFlightHits = new FlightHitsRule(flightId, ruleHits);
			flightHitsRules.add(ruleFlightHits);

			Integer watchlistHit = hitsSummaryRepository.watchlistHitCount(flightId);
			FlightHitsWatchlist watchlistHitCount = new FlightHitsWatchlist(flightId, watchlistHit);
			flightHitsWatchlists.add(watchlistHitCount);

			Integer graphWatchlistHit = hitsSummaryRepository.graphHitCount(flightId);
			FlightHitsGraph flightHitsGraph = new FlightHitsGraph(flightId, graphWatchlistHit);
			flightHitsGraphs.add(flightHitsGraph);

			Integer partialHitCount = hitsSummaryRepository.partialHitCount(flightId);
			FlightHitsFuzzy flightHitsFuzzy = new FlightHitsFuzzy(flightId, partialHitCount);
			flightHitsFuzzies.add(flightHitsFuzzy);
		}
		flightHitsRuleRepository.saveAll(flightHitsRules);
		flightHitsWatchlistRepository.saveAll(flightHitsWatchlists);
		flightGraphHitsRepository.saveAll(flightHitsGraphs);
		flightFuzzyHitsRepository.saveAll(flightHitsFuzzies);
	}
}
