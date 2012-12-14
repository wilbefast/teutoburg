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
package wjd.teutoburg.quadtree;

import java.util.Iterator;
import wjd.math.Circle;
import wjd.math.Rect;
import wjd.teutoburg.collision.Collider;
import wjd.teutoburg.collision.ICollisionManager;

/**
 *
 * @author wdyce
 * @since Dec 13, 2012
 */
public class QTCollisionManager implements ICollisionManager
{
  /* ATTRIBUTES */
  private QTNode quad_tree;
  private Rect bounds;
  
  /* METHODS */

  // constructors
  public QTCollisionManager(Rect bounds_)
  {
    this.bounds = bounds_;
    quad_tree = new QTNode(bounds_);
  }

  // accessors

  // mutators
  
  /* IMPLEMENTS -- IPHYSICSMANAGER */

  @Override
  public Iterable<Collider> getInRect(final Rect area)
  {
    return new Iterable<Collider>()
    {
      @Override
      public Iterator<Collider> iterator()
      {
        return new QTAreaChecker(quad_tree, area);
      }
    };
  }

  @Override
  public Iterable<Collider> getInCircle(Circle circle_query)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void register(Collider c)
  {
    quad_tree.insert(c);
  }

  @Override
  public void generateCollisions()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }



}
