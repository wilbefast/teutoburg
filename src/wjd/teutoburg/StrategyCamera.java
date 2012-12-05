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
package wjd.teutoburg;

import wjd.amb.control.EUpdateResult;
import wjd.amb.control.IInput;
import wjd.amb.view.ICamera;
import wjd.math.Rect;
import wjd.math.V2;

/**
 * A StrategyCamera that can be panned with the keyboard or mouse, and zoomed towards a
 * specific target (say, the mouse) so that the target is kept in the same place
 * relative to the view, as in Google Maps.
 *
 * @author wdyce
 * @since 05-Mar-2012
 */
public class StrategyCamera implements ICamera
{
  /* CONSTANTS */
  private static final int SCOLL_MOUSE_DISTANCE = 48;
  private static final int SCROLL_SPEED = 6;
  private static final float ZOOM_SPEED = 0.001f;
  private static final float ZOOM_MIN = 0.1f;
  private static final float ZOOM_MAX = 2.0f;
  private static final float ZOOM_DEFAULT = 1.0f;
  
  /* ATTRIBUTES */
  private V2 projection_size = new V2();
  private Rect view = new Rect(), boundary;
  private float zoom = ZOOM_DEFAULT;

  /* METHODS */
  // creation
  /**
   * Create a Real-time strategy camera by specifying the boundary rectangle the 
   * view should remain within, or null for no boundary: other parameters will
   * be set when it is attached to a canvas.
   *
   * @param boundary the Rectangle that the view must remain within, or null for
   * no boundary.
   */
  public StrategyCamera(Rect boundary)
  {
    this.boundary = boundary;
  }
  
  /* IMPLEMENTATIONS -- ICAMERA */
  
  public Rect getView()
  {
    return view;
  }

  // query
  @Override
  public float getZoom()
  {
    return zoom;
  }

  @Override
  public V2 getPerspective(V2 position)
  {
    return new V2((position.x - view.x) * zoom, (position.y - view.y) * zoom);
  }

  @Override
  public Rect getPerspective(Rect rect)
  {
    return new Rect(getPerspective(rect.pos()), rect.size().clone().scale(zoom));
  }

  @Override
  public V2 getGlobal(V2 position)
  {
    return new V2(position.x / zoom + view.x, position.y / zoom + view.y);
  }
  
  @Override
  public Rect getGlobal(Rect rect)
  {
    return new Rect(getGlobal(rect.pos()), rect.size().clone().scale(1/zoom));
  }

  @Override
  public boolean canSee(V2 position)
  {
    return view.contains(position);
  }
  
  @Override
  public boolean canSee(Rect area)
  {
    return area.collides(view);
  }

  // modification
  
  @Override
  public void reset()
  {
    view.reset(V2.ORIGIN, projection_size);
    zoom = ZOOM_DEFAULT;
  }
  
  @Override
  public void setProjectionSize(V2 projection_size)
  {
    this.projection_size = projection_size;
    view.size(projection_size.clone().scale(1.0f / zoom));
  }
  @Override
  public void setProjectionArea(Rect projection_area)
  {
    System.out.println("FIXME -- setProjectionArea");
  }

  @Override
  public void pan(V2 translation)
  {
    // move the view
    view.shift(translation.scale(1 / zoom));

    // don't stray out of bounds
    if (boundary != null)
      keepInsideBounds();
  }

  @Override
  public void zoom(float delta, V2 target)
  { 
    V2 target_true = getGlobal(target);
    V2 target_relative = new V2(projection_size.x / target.x,
                                projection_size.y / target.y);

    // reset zoom counter, don't zoom too much
    zoom += delta * zoom;
    if (zoom > ZOOM_MAX)
      zoom = ZOOM_MAX;
    else if (zoom < ZOOM_MIN)
      zoom = ZOOM_MIN;
    
    // perform the zoom
    view.size(projection_size.clone().scale(1.0f / zoom));
    view.x = target_true.x - view.w / target_relative.x;
    view.y = target_true.y - view.h / target_relative.y;
    
    // don't stray out of bounds
    if (boundary != null)
      keepInsideBounds();

  }
  
  
  /* IMPLEMENTATIONS -- IINTERACTIVE */
  
  @Override
  public EUpdateResult processInput(IInput input)
  {
    EUpdateResult result = processKeyboard(input);
    if(result != EUpdateResult.CONTINUE)
      return result;
    else
      return processMouse(input);
    
  }

  /* SUBROUTINES */
  private void keepInsideBounds()
  {
    V2 overlap = view.overlap(boundary);

    // pan view to keep within borders

    // pan view to keep within borders -- left/right
    if (overlap.x < 0)
    {
      // left
      if (view.x < boundary.x)
        view.x = boundary.x;
      // right
      else if (view.endx() > boundary.endx())
        view.x = boundary.endx() - view.w;
    }
    else if (overlap.x > 0)
      view.x = boundary.x - overlap.x * 0.5f;

    // pan view to keep within borders -- top/bottom
    if (overlap.y < 0)
    {
      // top
      if (view.y < boundary.y)
        view.y = boundary.y;
      // bottom
      else if (view.endy() > boundary.endy())
        view.y = boundary.endy() - view.h;
    }
    else if (overlap.y > 0)
      view.y = boundary.y - overlap.y * 0.5f;
  }
  
  private EUpdateResult processKeyboard(IInput input)
  {
    // move the camera
    pan(input.getKeyDirection().scale(SCROLL_SPEED));
    
    // keep on keeping on :)
    return EUpdateResult.CONTINUE;
  }
  
  private EUpdateResult processMouse(IInput input)
  {
    // mouse position
    V2 mouse_pos = input.getMousePosition(); 

    // mouse near edges = pan
    /*V2 scroll_dir = new V2();
    if (mouse_pos.x < SCOLL_MOUSE_DISTANCE)
      scroll_dir.x(-1);
    else if (mouse_pos.x > window_size.x - SCOLL_MOUSE_DISTANCE)
      scroll_dir.x(1);
    if (mouse_pos.y < SCOLL_MOUSE_DISTANCE)
      scroll_dir.y(-1);
    else if (mouse_pos.y > window_size.y - SCOLL_MOUSE_DISTANCE)
      scroll_dir.y(1);
    pan(scroll_dir.scale(SCROLL_SPEED));*/

    // mouse wheel = zoom
    int wheel = input.getMouseWheelDelta();
    if (wheel != 0)
      zoom(wheel * ZOOM_SPEED, mouse_pos);
    
    // keep on keeping on :)
    return EUpdateResult.CONTINUE;
  }
}
