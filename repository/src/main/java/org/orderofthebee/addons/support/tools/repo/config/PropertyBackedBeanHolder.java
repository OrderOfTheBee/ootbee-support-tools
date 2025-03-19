/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */
package org.orderofthebee.addons.support.tools.repo.config;

import org.alfresco.repo.management.subsystems.PropertyBackedBean;

/**
 * This class provides simple holder instances for {@link PropertyBackedBean} to simplify lookup in a list of known property backed bean
 * instances and avoid having to reconstruct the bean name.
 *
 * @author Axel Faust
 */
class PropertyBackedBeanHolder
{

    private final String name;

    private final PropertyBackedBean propertyBackedBean;

    protected PropertyBackedBeanHolder(final PropertyBackedBean propertyBackedBean)
    {
        this(null, propertyBackedBean);
    }

    protected PropertyBackedBeanHolder(final String name, final PropertyBackedBean propertyBackedBean)
    {
        this.name = name;
        this.propertyBackedBean = propertyBackedBean;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return the propertyBackedBean
     */
    public PropertyBackedBean getPropertyBackedBean()
    {
        return this.propertyBackedBean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.propertyBackedBean == null) ? 0 : this.propertyBackedBean.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj)
    {
        boolean equal;
        if (this == obj)
        {
            equal = true;
        }
        else if (obj == null)
        {
            equal = false;
        }
        else if (this.getClass() != obj.getClass())
        {
            equal = false;
        }
        else
        {
            final PropertyBackedBeanHolder other = (PropertyBackedBeanHolder) obj;
            if (this.propertyBackedBean == null)
            {
                if (other.propertyBackedBean != null)
                {
                    equal = false;
                }
                else
                {
                    equal = true;
                }
            }
            else if (!this.propertyBackedBean.equals(other.propertyBackedBean))
            {
                equal = false;
            }
            else
            {
                equal = true;
            }
        }
        return equal;
    }

}