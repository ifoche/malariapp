/*
 * Copyright (c) 2015.
 *
 * This file is part of QA App.
 *
 *  Health Network QIS App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Health Network QIS App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.data.database.iomodules.dhis.importer.models;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.AttributeFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.AttributeFlow_Table;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by arrizabalaga on 6/11/15.
 */
public class AttributeExtended {

    AttributeFlow attributeFlow;

    public AttributeExtended(AttributeFlow attributeFlow) {
        this.attributeFlow = attributeFlow;
    }

    public String getCode() {
        return attributeFlow.getCode();
    }

    public String getUid() {
        return attributeFlow.getAttributeUId();
    }

    public AttributeFlow getAttribute() {
        return attributeFlow;
    }

    /**
     * Find an attribute by its code
     */
    public static AttributeFlow findAttributeByCode(String code) {
        return new Select().from(AttributeFlow.class)
                .where(AttributeFlow_Table.code.is(code))
                .querySingle();
    }

    public static List<AttributeExtended> getExtendedList(List<AttributeFlow> flowList) {
        List<AttributeExtended> extendedsList = new ArrayList<>();
        for (AttributeFlow flowPojo : flowList) {
            extendedsList.add(new AttributeExtended(flowPojo));
        }
        return extendedsList;
    }
}
