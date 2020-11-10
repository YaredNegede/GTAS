/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Message;

import java.util.List;

public interface MessageService {

	List<Message> getMostRecent500Messages();

	RecentMessageInformation mostRecentId(Long passengerId, Long flightId);

}
