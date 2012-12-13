/*
 Copyright (C) 2012 William James Dyce

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wjd.teutoburg.collision;

import java.util.LinkedList;
import wjd.math.M;
import wjd.math.Rect;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 11, 2012
 */
public class ListCollisionManager implements ICollisionManager 
{
  /* ATTRIBUTES */
  private final LinkedList<Collider> objects = new LinkedList<Collider>();
  
  /* METHODS */

  // constructors

  // accessors

  // mutators
  
  /* IMPLEMENTS -- IPHYSICSMANAGER */
  
  @Override
  public Iterable<Collider> getInRect(Rect area)
  {
    LinkedList<Collider> result = new LinkedList<Collider>();
    for(Collider c : objects)
      if(area.contains(c.getPosition()))
        result.add(c);
    return result;
  }

  @Override
  public Iterable<Collider> getInCircle(V2 centre, float radius)
  {
    LinkedList<Collider> result = new LinkedList<Collider>();
    for(Collider c : objects)
      if(c.getPosition().distance2(centre) < M.sqr(radius) + M.sqr(c.getRadius()))
        result.add(c);
    return result;
  }

  @Override
  public void addObject(Collider c)
  {
    objects.add(c);
  }
}
