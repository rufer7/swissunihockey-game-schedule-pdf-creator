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
package be.rufer.swissunihockey;

public interface TestConstants {

    String TEAM_ID = "428660";
    String CLUB_ID = "441388";
    String SEASON = "2016";
    String LEAGUE = "1";
    String GAME_CLASS = "11";
    String GROUP = "Gruppe 1";
    String CALENDAR_STRING = "BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:icalendar-ruby\n" +
            "CALSCALE:GREGORIAN\n" +
            "END:VCALENDAR";
    String SAMPLE_PDF_FILE_NAME = "Sample-game-schedule.pdf";
    String TEAM_NAME = "Sample Team";
    String CLUB_NAME = "Sample Club";
}
