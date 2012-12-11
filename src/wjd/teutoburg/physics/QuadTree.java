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
package wjd.teutoburg.physics;

import wjd.math.Rect;

/**
 *
 * @author wdyce
 * @since Dec 11, 2012
 */
public class QuadTree 
{
  /* CONSTANTS */
  public static final int NODE_CAPACITY = 4;
  
  
  /* ATTRIBUTES */
  private final Physical[] objects = new Physical[NODE_CAPACITY];
  private int n_objects = 0;
  private QuadTree[] children;
  private final Rect area;
  
  /* METHODS */

  // constructors
  public QuadTree(Rect area_)
  {
    this.area = area_;
  }

  // accessors
  
  public boolean intersects(Rect query)
  {
    return area.collides(query);
  }
  
  public Physical getObject(int i)
  {
    return (i >= 0 && i < NODE_CAPACITY) ? objects[i] : null;
  }
  
  public QuadTree getChildTree(int i)
  {
    return (i >= 0 && i < 4) ? children[i] : null;
  }

  // mutators
  
  public boolean insert(Physical p)
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
    else if(children[0] == null)
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
    // local variables
    float sub_w = area.w * 0.5f, 
          sub_h = area.h * 0.5f,
          centre_x = area.x + sub_w,
          centre_y = area.y + sub_h;
    
    // create children
    children = new QuadTree[4];
    children[0] = new QuadTree(new Rect(area.x, area.y, sub_w, sub_h));
    children[1] = new QuadTree(new Rect(centre_x, area.y, sub_w, sub_h));
    children[2] = new QuadTree(new Rect(centre_x, centre_y, sub_w, sub_h));
    children[3] = new QuadTree(new Rect(area.x, centre_y, sub_w, sub_h));
  }
}
