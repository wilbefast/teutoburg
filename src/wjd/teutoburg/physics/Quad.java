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
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 11, 2012
 */
public class Quad 
{
  /* CONSTANTS */
  public static enum ECorner { NE, SE, SW, NW }
  public static final int QUAD_CAPACITY = 4;
  
  
  /* ATTRIBUTES */
  private final Physical[] objects = new Physical[QUAD_CAPACITY];
  private int n_objects = 0;
  private Quad[] children;
  private final Rect area;
  private final ECorner corner;
  
  /* METHODS */

  // constructors
  public Quad(Rect area_, ECorner corner_)
  {
    this.area = area_;
    this.corner = corner_;
  }

  // accessors

  // mutators
  
  public boolean insert(Physical p)
  {
    // ignore objects outside of this quad's area
    if(!area.contains(p.getPosition()))
      return false;
    
    // if this node's capacity has been reached, subdivide it
    if(n_objects < QUAD_CAPACITY)
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
    ECorner[] corners = ECorner.values();
    
    children = new Quad[4];
    
    //for(int i = 0; i < 4; i++)
    //children[i] = corners[i].getSubArea(area);
  }
}
