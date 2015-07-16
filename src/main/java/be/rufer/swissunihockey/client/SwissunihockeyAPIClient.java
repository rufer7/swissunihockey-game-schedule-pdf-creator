/*
 * Copyright (C) 2015 Marc Rufer (m.rufer@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.rufer.swissunihockey.client;

import be.rufer.swissunihockey.client.domain.ClubsResponse;
import be.rufer.swissunihockey.client.domain.ClubEntry;
import be.rufer.swissunihockey.client.exception.CalendarConversionException;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for interaction with the <a href="https://api-v2.swissunihockey.ch/api/doc">siwssunihockey API v2</a>
 */
@Service
public class SwissunihockeyAPIClient {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * @param teamId Id of the team
     * @return events from the range of 1 month in the past and 12 months in the future of the team with the given ID
     */
    public Calendar getCalendarForTeam(String teamId) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put(UrlVariables.TEAM_ID, teamId);
        String response = restTemplate.getForObject(UrlTemplates.GET_CALENDAR_FOR_TEAM, String.class, variables);
        return convertToCalendar(response);
    }

    /**
     * @param clubId Id of the club
     * @return events from the range of 1 month in the past and 12 months in the future of the team with the given ID
     */
    public Calendar getCalendarForClub(String clubId) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put(UrlVariables.CLUB_ID, clubId);
        String response = restTemplate.getForObject(UrlTemplates.GET_CALENDAR_FOR_CLUB, String.class, variables);
        return convertToCalendar(response);
    }

    /**
     * @param season the season (i.e. 2015 for season 2015/2016)
     * @param league the id of the league (i.e. 1 for HNLA)
     * @param gameClass the class of the games (i.e. 11 for HNLA group 1)
     * @param group the group (i.e. Gruppe 1)
     * @return events from the group and the season indicated
     */
    public Calendar getCalendarForGroup(String season, String league, String gameClass, String group) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put(UrlVariables.SEASON, season);
        variables.put(UrlVariables.LEAGUE, league);
        variables.put(UrlVariables.GAME_CLASS, gameClass);
        variables.put(UrlVariables.GROUP, group);
        String response = restTemplate.getForObject(UrlTemplates.GET_CALENDAR_FOR_GROUP, String.class, variables);
        return convertToCalendar(response);
    }

    private Calendar convertToCalendar(String response) {
        StringReader reader = new StringReader(response);
        CalendarBuilder builder = new CalendarBuilder();
        try {
            return builder.build(reader);
        } catch (IOException | ParserException e) {
            throw new CalendarConversionException();
        }
    }

    /**
     * @param season the season (i.e. 2015 for season 2015/2016)
     * @return all clubs of the given season
     */
    public Map<String, String> getClubsOfSeason(String season) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put(UrlVariables.SEASON, season);
        ClubsResponse response = restTemplate.getForObject(UrlTemplates.GET_CLUBS_OF_SEASON, ClubsResponse.class, variables);
        return getClubsMap(response);
    }

    private Map<String, String> getClubsMap(ClubsResponse response) {
        Map<String, String> clubs = new HashMap<>();
        for (ClubEntry clubEntry : response.getEntries()) {
            clubs.put(clubEntry.getContext().getClubId(), clubEntry.getText());
        }
        return clubs;
    }
}
