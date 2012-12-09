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
  protected final V2 position, direction, left;
  // view
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
  public final void deleteSoldiers()
  {
    soldiers = null;
  }
  
  public boolean refresh()
  {
    // reallocate vector if need be
    boolean regenerate = (soldiers == null);
    if(regenerate)
      soldiers = new Soldier[owner.getStrength()];

    // the arrow shows us which way the regiment is facing from afar
    float radius = owner.getRadius();
    // front of the regiment
    arrow_top.reset(direction).scale(radius * 0.5f).add(position);
    // left flank
    arrow_left.reset(left).scale(radius * 0.5f).add(position)
      .add(-direction.x * radius * 0.5f, -direction.y * radius * 0.5f);
    // right flank
    arrow_right.reset(left).scale(radius*0.5f).opp().add(position)
      .add(-direction.x * radius * 0.5f, -direction.y * 0.5f*radius);
    
    return regenerate;
  }
  
  /* INTERFACE */
  
  public abstract float rebuild();

  
  /* IMPLEMENTS -- IVISIBLE */
  @Override
  public void render(ICanvas canvas)
  {
    if(soldiers != null)
    {
      // draw soldiers if available
      for(Soldier s : soldiers)
        s.render(canvas);
    }
    else
    {
      // draw an imposter if not
      canvas.setColour(owner.getFaction().colour_shield);
      canvas.angleBox(position, direction, owner.getRadius(), true);
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
    public float rebuild()
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
      soldiers = null;
      refresh();
      
      // change the overall radius of the unit
      return (n_files * SPACING * 0.5f);
    }

    @Override
    public boolean refresh()
    {
      // regenerate soldiers if need be
      boolean regenerate = super.refresh();

      // reset position by rank and file
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
          if(regenerate)
            soldiers[r * n_files + f] 
              = owner.getFaction().createSoldier(soldier_position, direction);
          else
            soldiers[r * n_files + f].reposition(soldier_position, direction);
        }
      }
      return regenerate;
    }
  }

  public static class Rabble extends Formation
  {
    /* CONSTANTS */
    private static final float FULL_CIRCLE  = (float)(2 * Math.PI);
    private static final float LAYER_ANGLE_OFFSET  = (float)(FULL_CIRCLE / M.PHI);
    private static final float LAYER_RADIUS = 22.0f;

    
    /* ATTRIBUTES */
    private int n_layers;
    
    /* METHODS */
    // constructors
    public Rabble(RegimentAgent _owner)
    {
      super(_owner);
    }
    
    
    /* IMPLEMENTS -- FORMATION */
    @Override
    public float rebuild()
    {
      // calculate the number of layers
      n_layers = M.ilog2(owner.getStrength());
      
      // we must now reposition the soldiers in their new formation
      soldiers = null;
      refresh();
      
      // change the overall radius of the unit
      return (n_layers * LAYER_RADIUS);
    }

    @Override
    public boolean refresh()
    {
      // regenerate soldiers if need be
      boolean regenerate = super.refresh();
      
      // reset position by layer and spoke
      int layer_size = 1, soldier_i = 0;
      for (int l = 0; l < n_layers; l++)
      {
        float angle = l * LAYER_ANGLE_OFFSET, 
              angle_step = FULL_CIRCLE / layer_size;
        for(int s = 0; s < layer_size; s++)
        {
          // calculate absolute position and move there
          soldier_position.xy((float)Math.cos(angle), (float)Math.sin(angle))
                          .scale(layer_size * LAYER_RADIUS);
          
          // calculate absolute position and move there
          if(regenerate)
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
      return regenerate;
    }
  }
}
