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

public interface UrlTemplates {

    String GET_CALENDAR_FOR_TEAM = "https://api-v2.swissunihockey.ch/api/calendars?team_id={TEAM_ID}";
    String GET_CALENDAR_FOR_CLUB = "https://api-v2.swissunihockey.ch/api/calendars?club_id={CLUB_ID}";
    String GET_CALENDAR_FOR_GROUP =
            "https://api-v2.swissunihockey.ch/api/calendars?season={SEASON}&league={LEAGUE}&game_class={GAME_CLASS}&group={GROUP}";
    String GET_CLUBS_OF_SEASON = "https://api-v2.swissunihockey.ch/api/clubs?season={SEASON}";
    String GET_GAMES = "https://api-v2.swissunihockey.ch/api/games?mode=list";
}
