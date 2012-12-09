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
package wjd.teutoburg.regiment;

import java.util.Random;
import wjd.amb.view.Colour;
import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.M;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 7, 2012
 */
public abstract class Formation implements IVisible
{
  /* LOCAL VARIABLES */
  protected static V2 soldier_position = new V2();
  
  /* ATTRIBUTES */
  // model
  protected final RegimentAgent owner;
  protected Soldier[] soldiers = null;
  protected float radius;
  protected final V2 position, direction, left;
  // view
  private boolean detail = true;
  private final V2 arrow_left = new V2(), 
                  arrow_right = new V2(), 
                  arrow_top = new V2();
  
  /* METHODS */
  
  // constructors
  public Formation(RegimentAgent owner)
  {
    this.owner = owner;
    direction = owner.getDirection();
    position = owner.getPosition();
    left = owner.getLeft();
  }
  
  // mutators
  public void setDetail(boolean new_detail)
  {
    if(new_detail == detail)
      return;
    detail = new_detail;
    
    if(detail)
    {
      soldiers = new Soldier[owner.getStrength()];
      reposition();
    }
    else
      soldiers = null;
  }
  
  public void reposition()
  {
    // the arrow shows us which way the regiment is facing from afar
    arrow_top.reset(direction).scale(radius * 0.5f).add(position);
    arrow_left.reset(left).scale(radius * 0.5f).add(position)
      .add(-direction.x * radius * 0.5f, -direction.y * radius * 0.5f);
    arrow_right.reset(left).scale(radius*0.5f).opp().add(position)
      .add(-direction.x * radius * 0.5f, -direction.y * 0.5f*radius);
    
    // also refresh the soldiers if at the required detail level
    if(detail)
      repositionSoldiers();
  }
  
  /* INTERFACE */
  public abstract void repositionSoldiers();
  public abstract float reform();
  public abstract void renderImposter(ICanvas canvas);

  
  /* IMPLEMENTS -- IVISIBLE */
  @Override
  public void render(ICanvas canvas)
  {
   
    if(detail)
    {
      // draw soldiers if available
      for(Soldier s : soldiers)
        s.render(canvas);
    }
    else
    {
      // draw an imposter if not
      canvas.setColour(owner.getFaction().colour_shield);
      renderImposter(canvas);
      canvas.setColour(Colour.WHITE);
      canvas.triangle(arrow_left, arrow_top, arrow_right, true);
    }
  }

  /* IMPLEMENTATIONS */
  
  public static class Turtle extends Formation
  {
    /* CONSTANTS */
    private static final float SPACING = 22.0f;
    
    /* LOCAL VARIABLES */
    private static V2 r_offset = new V2(), 
                      f_offset = new V2();
    
    /* ATTRIBUTES */
    private int n_ranks;
    private float ranks_middle;
    private int n_files;
    private float files_middle;
    private int incomplete_rank;
    
    /* METHODS */
    
    // constructors
    public Turtle(RegimentAgent _owner)
    {
      super(_owner);
    }
    
    /* IMPLEMENTS -- FORMATION */
    @Override
    public float reform()
    {
      // get local variables
      int strength = owner.getStrength();

      // calculate number of ranks and files, plus size of incomplete final rank
      n_files = M.isqrt(strength);
      files_middle = (n_files - 1) * SPACING * 0.5f;
      n_ranks = strength / n_files;
      ranks_middle = (n_ranks - 1) * SPACING * 0.5f;
      incomplete_rank = strength - (n_files * n_ranks);

      // we must now reposition the soldiers in their new formation
      soldiers = new Soldier[strength];
      reposition();
      
      // change the overall radius of the unit
      return (radius = n_files * SPACING * 0.5f);
    }

    @Override
    public void repositionSoldiers()
    {
      // reset position by rank and file
      int soldier_i = 0;
      for (int r = 0; r < (n_ranks + 1); r++)
      {
        // row offset
        r_offset.reset(direction).scale(ranks_middle -(r * SPACING));
        for (int f = 0; f < ((r < n_ranks) ? n_files : incomplete_rank); f++)
        {
          // file offset
          f_offset.reset(left).scale((f * SPACING) - files_middle);

          // calculate absolute position and move there
          soldier_position.reset(position).add(r_offset).add(f_offset);
          if(soldiers[soldier_i] == null)
            soldiers[soldier_i] 
              = owner.getFaction().createSoldier(soldier_position, direction);
          else
            soldiers[soldier_i].reposition(soldier_position, direction);
          soldier_i++;
        }
      }
    }
    
    @Override
    public void renderImposter(ICanvas canvas)
    {
      canvas.angleBox(position, direction, owner.getRadius(), true);
    }
  }

  public static class Rabble extends Formation
  {
    /* CONSTANTS */
    
    private static final float FULL_CIRCLE  = (float)(2 * Math.PI);
    private static final float LAYER_ANGLE_OFFSET  = (float)(FULL_CIRCLE / M.PHI);
    private static final float LAYER_RADIUS = 26.0f;
    private static final float RADIUS_VAR = LAYER_RADIUS * 0.4f;
    private static final float ANGLE_VAR = 0.3f;

    
    /* ATTRIBUTES */
    private int n_layers, incomplete_layer;
    private final long seed; 
    private final Random randomiser;
    
    /* METHODS */
    // constructors
    public Rabble(RegimentAgent _owner)
    {
      super(_owner);
      
      randomiser = new Random();
      seed = randomiser.nextLong();
    }
    
    
    /* IMPLEMENTS -- FORMATION */
    @Override
    public float reform()
    {
      int strength = owner.getStrength();
      
      // calculate the number of layers
      n_layers = M.ilog2(strength);
      incomplete_layer = strength - (M.ipow2(n_layers) - 1);
      
      // we must now reposition the soldiers in their new formation
      soldiers = new Soldier[strength];
      reposition();
      
      // change the overall radius of the unit
      return (radius = n_layers * LAYER_RADIUS);
    }

    @Override
    public void repositionSoldiers()
    {
      // reset randomiser so the soldiers don't dance around
      randomiser.setSeed(seed);
      
      // reset position by layer and spoke
      int layer_size = 1, soldier_i = 0;
      for (int l = 0; l < (n_layers+1); l++)
      {
        float angle = (float)(M.PHI * randomiser.nextDouble()),
              angle_step = FULL_CIRCLE / layer_size;
        for(int s = 0; s < ((l < n_layers) ? layer_size : incomplete_layer); s++)
        {
          // calculate absolute position and move there
          float angle_noise = angle + signedRand(0.3f)*angle_step,
                radius_noise = l * (LAYER_RADIUS + signedRand(RADIUS_VAR));
          
          soldier_position.xy((float)Math.cos(angle_noise), 
                              (float)Math.sin(angle_noise))
                          .scale(radius_noise)
                          .add(position);

          // calculate absolute position and move there
          if(soldiers[soldier_i] == null)
            soldiers[soldier_i] 
              = owner.getFaction().createSoldier(soldier_position, direction);
          else
            soldiers[soldier_i].reposition(soldier_position, direction);

          // increment counters
          angle += angle_step;
          soldier_i++;
        }
        layer_size *= 2;
      }
      System.out.println("");
    }
    
    @Override
    public void renderImposter(ICanvas canvas)
    {
      canvas.circle(position, owner.getRadius(), true);
    }
    
    /* SUBROUTINES */
    
    private float signedRand(float value)
    {
      float r = randomiser.nextFloat();
      return (r < 0.5f) ? value*2*r : value*2*(r-0.5f);
    }
  }
}
