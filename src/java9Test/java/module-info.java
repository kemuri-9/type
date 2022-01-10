/**
 * Copyright 2022 Steven Walters
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Type Tests
 */
@SuppressWarnings({"module", "requires-automatic", "requires-transitive-automatic"})
open module net.kemuri9.type.test {
    requires java.base;

    requires transitive org.apache.commons.lang3;
    requires transitive org.junit.jupiter.api;
    requires transitive org.junit.jupiter.params;

    requires transitive net.kemuri9.type;

    exports net.kemuri9.type.test;
}
