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
package wjd.teutoburg.forest;

import java.util.Random;
import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.M;
import wjd.math.Rect;
import wjd.math.V2;
import wjd.teutoburg.collision.Collider;
import wjd.teutoburg.simulation.Palette;

/**
 * A small group of trees.
 * 
 * @author wdyce
 * @since Dec 14, 2012
 */
public class Copse extends Collider implements IVisible
{
  /* CONSTANTS */
  public static final int N_TREES = 50;
  public static final float NUMBER_FACTOR = 0.4f;
  public static final float SIZE = Tree.COLLISION_RADIUS * N_TREES;
  public static final float SIZE_VAR = 0.5f; // percent
  private static final float ZOOM_IMPOSTER_THRESHOLD = 0.15f;
  
  /* ATTRIBUTES */
  // generation
  private final long seed; 
  private final Random randomiser = new Random();
  private final V2 tree_position = new V2();
  // view
  private boolean detail = true;
  private final int n_trees;
  private Tree[] trees = null;
  private boolean visible = true, nearby = true;
  
  /* METHODS */
  public Copse(V2 position_)
  {
    super(position_);
    
    
    float size_var = (float)M.signedRand(SIZE_VAR, randomiser);
    setRadius((1.0f + size_var) * SIZE);
    n_trees = (int)((1.0f + size_var) * N_TREES);
    seed = randomiser.nextLong();
    
    generateTrees();
  }

  // constructors

  // accessors

  // mutators
  
  /* SUBROUTINES */
  
  private void generateTrees()
  {
    trees = new Tree[n_trees];
    randomiser.setSeed(seed);
    for(int t = 0; t < N_TREES; t++)
    {
      c.randomPoint(tree_position);
      Tree tree = new Tree(tree_position);
      trees[t] = tree;
    }
  }
  
  /* IMPLEMENTS -- IVISIBLE */
  
  @Override
  public void render(ICanvas canvas)
  {
    // draw if inside the camera's field of view
    if(visible = (canvas.getCamera().canSee(c)))
    {
      // we'll turn off the details if we're too far away
      nearby = (canvas.getCamera().getZoom() >= ZOOM_IMPOSTER_THRESHOLD);
     
      // render high-detail?
      if(nearby)
      {
        if(trees != null) for(int t = 0; t < N_TREES; t++)
          trees[t].render(canvas);
        else
          generateTrees();
      }
      // render low-detail
      else 
      {
        trees = null;
        canvas.setColour(Palette.COPSE_IMPOSTER);
        canvas.circle(c.centre, c.radius, true);
      }
    }
    // clear detail objects if not in view
    else
    {
      nearby = false;
      trees = null;
    }
  }

  
  /* IMPLEMENTS -- COLLIDERS */
  
  @Override
  public void boundaryEvent(Rect boundary)
  {
    // do nothing
  }

  @Override
  public void collisionEvent(Collider a)
  {
    // do nothing
  }
}
