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
import wjd.math.Rect;
import wjd.teutoburg.collision.Collider;

/**
 *
 * @author wdyce
 * @since Dec 11, 2012
 */
public class QTAreaChecker implements Iterator<Collider>
{
  /* ATTRIBUTES */
  private final Rect query_area;
  private QTNode root;
  private QTContentsIterator contents;
  private QTBranchIterator children;
  private QTAreaChecker recursive;
 
  
  /* METHODS */
  
  // constructors
  public QTAreaChecker(QTNode root_, Rect query_area_)
  {
    // save attributes
    this.root = root_;
    this.query_area = query_area_;
    
    // create sub-iterators
    contents = new QTContentsIterator(root);
    children = new QTBranchIterator(root);
  }
  
  /* IMPLEMENTS -- ITERATOR<PHYISCAL> */  
  
  @Override
  public boolean hasNext()
  {
    // break immediately if the root is outside of the area of interest
    if(!query_area.collides(root.area))
      return false;
    
    // are there any more objects in the root node?
    else if(contents.hasNext())
      return true;
    
    // is the root node of the search is at the bottom of the tree?
    else if(root.isLeaf())
      return false;
    
    // check recursive search, if there is one in execution
    else if(recursive != null && recursive.hasNext())
      return true;
    
    else 
    {
      // if the current recursive search has ended, launch a new one if possible
      while(children.hasNext())
      {
        recursive = new QTAreaChecker(children.next(), query_area);
        if(recursive.hasNext())
          return true;
      }
    }
    
    // if there are no more branches the entire tree has been explored
    return false;
  }

  @Override
  public Collider next()
  {
    if(contents.hasNext())
      return contents.next();
    
    else if(recursive.hasNext())
      return recursive.next();
    
    return null;
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException("Remove is not supported.");
  }
}
