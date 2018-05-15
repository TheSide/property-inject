/*******************************************************************************
 * Copyright (C) 2018 xlate.io LLC, http://www.xlate.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package io.xlate.inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class PropertyResourceFormatTest {

    @Test
    public void testValues() {
        PropertyResourceFormat[] values = PropertyResourceFormat.values();
        assertEquals(values[0].toString(), "PROPERTIES");
        assertEquals(values[1].toString(), "XML");
    }

    @Test
    public void testValueOf() {
        assertEquals(PropertyResourceFormat.PROPERTIES, PropertyResourceFormat.valueOf("PROPERTIES"));
        assertEquals(PropertyResourceFormat.XML, PropertyResourceFormat.valueOf("XML"));
    }

}
