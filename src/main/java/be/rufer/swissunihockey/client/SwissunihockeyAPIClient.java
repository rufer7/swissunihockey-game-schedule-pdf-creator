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

import be.rufer.swissunihockey.client.domain.*;
import be.rufer.swissunihockey.client.exception.CalendarConversionException;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(SwissunihockeyAPIClient.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * @param teamId Id of the team
     * @return events from the range of 1 month in the past and 12 months in the future of the team with the given ID
     */
    public Calendar getCalendarForTeam(String teamId) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put(UrlVariables.TEAM_ID, teamId);
        LOG.info("Get calendar for team from swissunihockey API");
        String response = restTemplate.getForObject(UrlTemplates.GET_CALENDAR_FOR_TEAM, String.class, variables);
        LOG.debug("GET {} - {}", UrlTemplates.GET_CALENDAR_FOR_TEAM, response);
        return convertToCalendar(response);
    }

    /**
     * @param clubId Id of the club
     * @return events from the range of 1 month in the past and 12 months in the future of the team with the given ID
     */
    public Calendar getCalendarForClub(String clubId) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put(UrlVariables.CLUB_ID, clubId);
        LOG.info("Get calendar for club from swissunihockey API");
        String response = restTemplate.getForObject(UrlTemplates.GET_CALENDAR_FOR_CLUB, String.class, variables);
        LOG.debug("GET {} - {}", UrlTemplates.GET_CALENDAR_FOR_CLUB, response);
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
        LOG.info("Get calendar for group from swissunihockey API");
        String response = restTemplate.getForObject(UrlTemplates.GET_CALENDAR_FOR_GROUP, String.class, variables);
        LOG.debug("GET {} - {}", UrlTemplates.GET_CALENDAR_FOR_GROUP, response);
        return convertToCalendar(response);
    }

    private Calendar convertToCalendar(String response) {
        LOG.info("Starting conversion to calendar...");
        LOG.debug("Response to convert: {}", response);
        StringReader reader = new StringReader(response);
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar;
        try {
            calendar = builder.build(reader);
        } catch (IOException | ParserException e) {
            throw new CalendarConversionException();
        }
        LOG.info("Conversion successfully completed");
        return calendar;
    }

    /**
     * @param season the season (i.e. 2015 for season 2015/2016)
     * @return all clubs of the given season as Map (key = id, value = textual representation)
     */
    public Map<String, String> getClubsOfSeason(String season) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put(UrlVariables.SEASON, season);
        LOG.info("Get clubs of season {}", season);
        ClubsResponse response = restTemplate.getForObject(UrlTemplates.GET_CLUBS_OF_SEASON, ClubsResponse.class, variables);
        return extractClubsToMap(response);
    }

    private Map<String, String> extractClubsToMap(ClubsResponse response) {
        LOG.info("Starting extraction of clubs...");
        Map<String, String> clubs = new HashMap<>();
        for (ClubEntry clubEntry : response.getEntries()) {
            clubs.put(clubEntry.getContext().getClubId(), clubEntry.getText());
        }
        LOG.info("Extraction of clubs successfully completed");
        LOG.debug("Extraction result: {}", clubs);
        return clubs;
    }

    /**
     * @return all leagues as Map (key = id, value = textual representation)
     */
    public Map<String, String> getLeagues() {
        LOG.info("Get leagues");
        GamesResponse response = restTemplate.getForObject(UrlTemplates.GET_GAMES, GamesResponse.class);
        return extractLeaguesToMap(response);
    }

    private Map<String, String> extractLeaguesToMap(GamesResponse response) {
        LOG.info("Starting extraction of leagues...");
        Map<String, String> leagues = new HashMap<>();
        for (Tab tab : response.getGameData().getTabs()) {
            if (null != tab.getLink()) {
                leagues.put(String.valueOf(tab.getLink().getLeagueEntry().getLeagueId()), tab.getText());
            } else {
                for (OtherLeagueEntry otherLeagueEntry : tab.getOtherLeagueEntries()) {
                    leagues.put(String.valueOf(otherLeagueEntry.getLeagueEntry().getLeagueId()), otherLeagueEntry.getText());
                }
            }
        }
        LOG.info("Extraction of leagues successfully completed...");
        LOG.debug("Extraction result: {}", leagues);
        return leagues;
    }
}
