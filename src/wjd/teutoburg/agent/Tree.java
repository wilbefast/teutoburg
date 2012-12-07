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
package wjd.teutoburg.agent;

import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.Rect;
import wjd.math.V2;
import wjd.teutoburg.Palette;

/**
 *
 * @author wdyce
 * @since Dec 6, 2012
 */
public class Tree implements IVisible, IPhysical
{
  /* CONSTANTS */
  public static final float SHADOW_RADIUS = 5.0f;
  // top of the tree
  public static final float SUMMIT_H = 30.0f;
  public static final float SUMMIT_H_VAR = SUMMIT_H*0.3f; // 30% random
  // trunk
  public static final float TRUNK_W = 3.0f;
  public static final float TRUNK_RADIUS = TRUNK_W*0.5f;
  public static final float TRUNK_H = SUMMIT_H*0.2f;
  // branches
  public static final float BRANCHES_W = 16.0f;
  public static final float BRANCHES_RADIUS = BRANCHES_W*0.5f;
  // model
  public static final float COLLISION_RADIUS = 10.0f;
  // visibility 
  private static final float ZOOM_IMPOSTER_THRESHOLD = 0.7f;
  
  /* ATTRIBUTES */
  private V2 position, summit, left, right, imposter_left, imposter_right;
  private Rect trunk;
  private boolean nearby = true;
  
  
  /* METHODS */
  
  // constructors
  public Tree(V2 position)
  {
    this.position = position;
    loadDetailedModel();
  }
  
  // accessors
  public V2 getPosition()
  {
    return position;
  }
  
  /* IMPLEMENTS -- IVISIBLE */

  @Override
  public void render(ICanvas canvas)
  {
    // check if in view
    if(canvas.getCamera().canSee(position))
    {
      // check if nearby
      if(canvas.getCamera().getZoom() >= ZOOM_IMPOSTER_THRESHOLD)
      {
        // check if the tree has just started being nearby
        if(!nearby)
        {
          loadDetailedModel();
          nearby = true;
        }
        
        // shadow
        canvas.setColour(Palette.GRASS_SHADOW);
        canvas.circle(position, SHADOW_RADIUS, true);
        // trunk
        canvas.setColour(Palette.TREE_TRUNK);
        canvas.box(trunk, true);
        // leaves
        canvas.setColour(Palette.TREE_LEAVES);
        canvas.triangle(summit, left, right, true);
      }
      else
      {
        // check if the tree has just stopped being nearby
        if(nearby)
        {
          loadImposterModel();
          nearby = false;
        }
        canvas.setColour(Palette.TREE_IMPOSTER);
        canvas.triangle(summit, imposter_left, imposter_right, true);
      }
    }
  }
  
  /* IMPLEMENTS -- IPHYSICAL */

  @Override
  public boolean isColliding(IPhysical other)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  /* SUBROUTINES */
  
  private void clearModels()
  {
    summit = left = right = imposter_left = imposter_right = null;
    trunk = null;
  }
  
  private void loadDetailedModel()
  {
    // free imposter
    imposter_left = imposter_right = null;
    
    // generate detailed model
    if(summit == null) 
      summit = new V2().xy(position.x, position.y - SUMMIT_H - SUMMIT_H_VAR*0.5f 
                                      + (float)(Math.random()*SUMMIT_H_VAR));
    left = new V2().xy(position.x - BRANCHES_RADIUS, position.y - TRUNK_H);
    right = new V2().xy(position.x + BRANCHES_RADIUS, position.y - TRUNK_H);
    trunk = new Rect(TRUNK_W, TRUNK_H).xy(position.x - TRUNK_RADIUS, 
                                          position.y - TRUNK_H - 1);
  }

  private void loadImposterModel()
  {
    // free detailed model
    left = right = null;
    trunk = null;
    
    // generate imposter model
    if(summit == null) 
      summit = new V2().xy(position.x, position.y - SUMMIT_H - SUMMIT_H_VAR*0.5f 
                                      + (float)(Math.random()*SUMMIT_H_VAR));
    imposter_left = new V2().xy(position.x - BRANCHES_W, position.y + TRUNK_H);
    imposter_right = new V2().xy(position.x + BRANCHES_W, position.y + TRUNK_H);
  }
}
