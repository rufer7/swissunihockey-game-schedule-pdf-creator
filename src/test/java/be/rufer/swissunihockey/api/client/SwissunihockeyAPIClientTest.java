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
package be.rufer.swissunihockey.api.client;

import be.rufer.swissunihockey.api.client.exception.CalendarConversionException;
import net.fortuna.ical4j.model.Calendar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SwissunihockeyAPIClientTest {

    private static final String TEAM_ID = "1";
    private static final String CLUB_ID = "9";
    private static final String SAMPLE_CALENDAR_STRING = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:icalendar-ruby\n" +
            "CALSCALE:GREGORIAN\n" +
            "END:VCALENDAR";

    @Mock
    private RestTemplate mockedRestTemplate;

    @InjectMocks
    private SwissunihockeyAPIClient swissunihockeyAPIClient = new SwissunihockeyAPIClient();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCalendarForTeamCallsSwissunihockeyAPI() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("TEAM_ID", TEAM_ID);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_TEAM), eq(String.class), eq(variables))).thenReturn(SAMPLE_CALENDAR_STRING);
        swissunihockeyAPIClient.getCalendarForTeam(TEAM_ID);
        verify(mockedRestTemplate).getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_TEAM), eq(String.class), eq(variables));
    }

    @Test
    public void getCalendarForTeamReturnsValidCalendar() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("TEAM_ID", TEAM_ID);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_TEAM), eq(String.class), eq(variables))).thenReturn(SAMPLE_CALENDAR_STRING);
        Calendar calendar = swissunihockeyAPIClient.getCalendarForTeam(TEAM_ID);
        assertNotNull(calendar);
    }

    @Test(expected = CalendarConversionException.class)
    public void getCalendarForTeamThrowsCalendarConversionExceptionForInvalidResponse() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("TEAM_ID", TEAM_ID);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_TEAM), eq(String.class), eq(variables))).thenReturn("");
        swissunihockeyAPIClient.getCalendarForTeam(TEAM_ID);
    }

    @Test
    public void getCalendarForClubCallsSwissunihockeyAPI() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("CLUB_ID", CLUB_ID);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_CLUB), eq(String.class), eq(variables))).thenReturn(SAMPLE_CALENDAR_STRING);
        swissunihockeyAPIClient.getCalendarForClub(CLUB_ID);
        verify(mockedRestTemplate).getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_CLUB), eq(String.class), eq(variables));
    }

    @Test
    public void getCalendarForClubReturnsValidCalendar() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("CLUB_ID", CLUB_ID);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_CLUB), eq(String.class), eq(variables))).thenReturn(SAMPLE_CALENDAR_STRING);
        Calendar calendar = swissunihockeyAPIClient.getCalendarForClub(CLUB_ID);
        assertNotNull(calendar);
    }

    @Test(expected = CalendarConversionException.class)
    public void getCalendarForClubThrowsCalendarConversionExceptionForInvalidResponse() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("CLUB_ID", CLUB_ID);
        when(mockedRestTemplate.getForObject(eq(UrlTemplates.GET_CALENDAR_FOR_CLUB), eq(String.class), eq(variables))).thenReturn("");
        swissunihockeyAPIClient.getCalendarForClub(CLUB_ID);
    }

    @Test
    public void getCalendarForGroupCallsSwissunihockeyAPI() {
//        To get a calendar for a group, use season, league, game_class and group arguments.
    }
}
