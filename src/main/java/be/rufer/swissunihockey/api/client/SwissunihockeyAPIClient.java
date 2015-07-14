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
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

@Service
public class SwissunihockeyAPIClient {

    @Autowired
    private RestTemplate restTemplate;

    public Calendar getCalendarForTeam(String teamId) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("TEAM_ID", teamId);
        String response = restTemplate.getForObject(UrlTemplates.GET_CALENDAR_FOR_TEAM, String.class, variables);
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
}
