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
import net.fortuna.ical4j.model.Calendar;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SwissunihockeyAPIClientTest {

    private static final String TEAM_ID = "1";
    private static final String CLUB_ID = "9";
    private static final String SEASON = "2015";
    private static final String LEAGUE = "1";
    private static final String GAME_CLASS = "11";
    private static final String GROUP = "Gruppe 1";
    private static final String SAMPLE_CALENDAR_STRING = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:icalendar-ruby\n" +
            "CALSCALE:GREGORIAN\n" +
            "END:VCALENDAR";

    private static ClubsResponse sampleClubsResponse;
    private static GamesResponse sampleGamesResponse;

    private Map<String, String> variables;

    @Mock
    private RestTemplate mockedRestTemplate;

    @InjectMocks
    private SwissunihockeyAPIClient swissunihockeyAPIClient;

    @BeforeClass
    public static void setup() {
        initSampleClubResponse();
        initSampleGamesResponse();
    }

    private static void initSampleGamesResponse() {
        List<Tab> tabs = new ArrayList<>();
        tabs.add(Tab.builder().text("HNLA").link(Link.builder().leagueEntry(LeagueEntry.builder().leagueId(1).build()).build()).build());
        List<OtherLeagueEntry> otherLeagueEntries = new ArrayList<>();
        otherLeagueEntries.add(OtherLeagueEntry.builder().text("Herren Aktive GF 1. Liga").leagueEntry(LeagueEntry.builder().leagueId(2).build()).build());
        tabs.add(Tab.builder().text("andere").otherLeagueEntries(otherLeagueEntries).build());
        sampleGamesResponse = GamesResponse.builder().gameData(GameData.builder().tabs(tabs).build()).build();
    }

    private static void initSampleClubResponse() {
        List<ClubEntry> entries = new ArrayList<>();
        entries.add(ClubEntry.builder().text("Sample Club").context(ClubEntryContext.builder().clubId("99").build()).build());
        sampleClubsResponse = ClubsResponse.builder().entries(entries).build();
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        variables = new HashMap<>();
    }

    @Test
    public void getCalendarForTeamCallsSwissunihockeyAPI() {
        variables.put(UrlVariables.TEAM_ID, TEAM_ID);
                when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_TEAM), eq(String.class), eq(variables))).thenReturn(SAMPLE_CALENDAR_STRING);
        swissunihockeyAPIClient.getCalendarForTeam(TEAM_ID);
        verify(mockedRestTemplate).getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_TEAM), eq(String.class), eq(variables));
    }

    @Test
    public void getCalendarForTeamReturnsValidCalendar() {
        variables.put(UrlVariables.TEAM_ID, TEAM_ID);
                when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_TEAM), eq(String.class), eq(variables))).thenReturn(SAMPLE_CALENDAR_STRING);
        Calendar calendar = swissunihockeyAPIClient.getCalendarForTeam(TEAM_ID);
        assertNotNull(calendar);
    }

    @Test(expected = CalendarConversionException.class)
    public void getCalendarForTeamThrowsCalendarConversionExceptionForInvalidResponse() {
        variables.put(UrlVariables.TEAM_ID, TEAM_ID);
                when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_TEAM), eq(String.class), eq(variables))).thenReturn("");
        swissunihockeyAPIClient.getCalendarForTeam(TEAM_ID);
    }

    @Test
    public void getCalendarForClubCallsSwissnihockeyAPI() {
        variables.put(UrlVariables.CLUB_ID, CLUB_ID);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_CLUB), eq(String.class), eq(variables))).thenReturn(SAMPLE_CALENDAR_STRING);
        swissunihockeyAPIClient.getCalendarForClub(CLUB_ID);
        verify(mockedRestTemplate).getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_CLUB), eq(String.class), eq(variables));
    }

    @Test
    public void getCalendarForClubReturnsValidCalendar() {
        variables.put(UrlVariables.CLUB_ID, CLUB_ID);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_CLUB), eq(String.class), eq(variables))).thenReturn(SAMPLE_CALENDAR_STRING);
        Calendar calendar = swissunihockeyAPIClient.getCalendarForClub(CLUB_ID);
        assertNotNull(calendar);
    }

    @Test(expected = CalendarConversionException.class)
    public void getCalendarForClubThrowsCalendarConversionExceptionForInvalidResponse() {
        variables.put(UrlVariables.CLUB_ID, CLUB_ID);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_CLUB), eq(String.class), eq(variables))).thenReturn("");
        swissunihockeyAPIClient.getCalendarForClub(CLUB_ID);
    }

    @Test
    public void getCalendarForGroupCallsSwissunihockeyAPI() {
        variables.put(UrlVariables.SEASON, SEASON);
        variables.put(UrlVariables.LEAGUE, LEAGUE);
        variables.put(UrlVariables.GAME_CLASS, GAME_CLASS);
        variables.put(UrlVariables.GROUP, GROUP);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_GROUP), eq(String.class), eq(variables))).thenReturn(SAMPLE_CALENDAR_STRING);
        swissunihockeyAPIClient.getCalendarForGroup(SEASON, LEAGUE, GAME_CLASS, GROUP);
        verify(mockedRestTemplate).getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_GROUP), eq(String.class), eq(variables));
    }

    @Test(expected = CalendarConversionException.class)
    public void getCalendarForGroupThrowsCalendarConversionExceptionForInvalidResponse() {
        variables.put(UrlVariables.SEASON, SEASON);
        variables.put(UrlVariables.LEAGUE, LEAGUE);
        variables.put(UrlVariables.GAME_CLASS, GAME_CLASS);
        variables.put(UrlVariables.GROUP, GROUP);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_GROUP), eq(String.class), eq(variables))).thenReturn("");
        swissunihockeyAPIClient.getCalendarForGroup(SEASON, LEAGUE, GAME_CLASS, GROUP);
    }

    @Test
    public void getClubsOfSeasonCallsSwissunihockeyAPI() {
        variables.put(UrlVariables.SEASON, SEASON);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CLUBS_OF_SEASON), eq(ClubsResponse.class), eq(variables))).thenReturn(sampleClubsResponse);
        swissunihockeyAPIClient.getClubsOfSeason(SEASON);
        verify(mockedRestTemplate).getForObject(eq(UrlTemplates.GET_CLUBS_OF_SEASON), eq(ClubsResponse.class), eq(variables));
    }

    @Test
    public void getClubsOfSeasonReturnsMapContainingIdAndNameOfClubs() {
        variables.put(UrlVariables.SEASON, SEASON);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CLUBS_OF_SEASON), eq(ClubsResponse.class), eq(variables)))
                .thenReturn(sampleClubsResponse);
        Map<String, String> clubs = swissunihockeyAPIClient.getClubsOfSeason(SEASON);
        assertEquals(1, clubs.size());
        assertEquals("Sample Club", clubs.get("99"));
    }

    @Test
    public void getLeaguesCallsGameEndpointOfSwissunihockeyAPI() {
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_GAMES), eq(GamesResponse.class))).thenReturn(sampleGamesResponse);
        swissunihockeyAPIClient.getLeagues();
        verify(mockedRestTemplate).getForObject(eq(UrlTemplates.GET_GAMES), eq(GamesResponse.class));
    }

    @Test
    public void getLeaguesReturnsMapContainingIdAndNameOfLeagues() {
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_GAMES), eq(GamesResponse.class))).thenReturn(sampleGamesResponse);
        Map<String, String> leagues = swissunihockeyAPIClient.getLeagues();
        assertEquals(2, leagues.size());
        assertEquals("HNLA", leagues.get("1"));
        assertEquals("Herren Aktive GF 1. Liga", leagues.get("2"));
    }
}
