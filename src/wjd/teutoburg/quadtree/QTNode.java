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

import wjd.math.Rect;
import wjd.teutoburg.collision.Collider;

/**
 *
 * @author wdyce
 * @since Dec 11, 2012
 */
public class QTNode 
{
  /* CONSTANTS */
  public static final int NODE_CAPACITY = 4;
  
  
  /* ATTRIBUTES */
  private final Collider[] objects = new Collider[NODE_CAPACITY];
  private int n_objects = 0;
  private QTNode[] children;
  final Rect area;
  private boolean leaf = true;
  
  /* METHODS */

  // constructors
  public QTNode(Rect area_)
  {
    this.area = area_;
  }

  // accessors
  
  public boolean intersects(Rect query)
  {
    return area.collides(query);
  }
  
  public int getNObjects()
  {
    return n_objects;
  }
  
  public Collider getObject(int i)
  {
    return (i >= 0 && i < NODE_CAPACITY) ? objects[i] : null;
  }
  
  public boolean isLeaf()
  {
    return leaf;
  }
  
  public QTNode getChildTree(int i)
  {
    return (i >= 0 && i < 4) ? children[i] : null;
  }
  // mutators
  
  public boolean insert(Collider p)
  {
    // ignore objects outside of this quad's area
    if(!area.contains(p.getPosition()))
      return false;
    
    // if this node's capacity has been reached, subdivide it
    if(n_objects < NODE_CAPACITY)
    {
      objects[n_objects] = p;
      return true;
    }
    // otherwise we may need to subdivide
    else if(leaf)
      subdivide();
      
    // add to the first appropriate child instead
    for(int i = 0; i < 4; i++)
      if(children[i].insert(p))
        return true;
    
    // point could not be added
    return false;
  }
  
  /* SUBROUTINES */
  
  private void subdivide()
  {
    leaf = false;
    
    // local variables
    float sub_w = area.w * 0.5f, 
          sub_h = area.h * 0.5f,
          centre_x = area.x + sub_w,
          centre_y = area.y + sub_h;
    
    // create children
    children = new QTNode[4];
    children[0] = new QTNode(new Rect(area.x, area.y, sub_w, sub_h));
    children[1] = new QTNode(new Rect(centre_x, area.y, sub_w, sub_h));
    children[2] = new QTNode(new Rect(centre_x, centre_y, sub_w, sub_h));
    children[3] = new QTNode(new Rect(area.x, centre_y, sub_w, sub_h));
  }
}
