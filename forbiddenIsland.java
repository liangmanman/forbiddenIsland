
// Assignment 9
// Liang Manman
// partner1-liangmanman
// Wang Jiameng
// partner2-kaimchai


import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import javalib.colors.*;
import javalib.worldimages.*;

// define the player class to represent the player
class Player {
    int x;
    int y;
    Cell pivot; // cell connect the player and the board
    
    // the constructor
    Player(Cell pivot) {
        this.pivot = pivot;
        this.x = this.pivot.x;
        this.y = this.pivot.y;
    }
    
    // use another pivot to connect the player and the board
    public void updatePlayer(Cell anewPivot) {
        this.pivot = anewPivot;
        this.x = this.pivot.x;
        this.y = this.pivot.y;
    }
    
    // represent the player image
    public WorldImage pilot() {
        return new DiskImage(new Posn(this.x * 10 + 5, this.y * 10 + 5), 10, new Yellow());
    }
}

// represent the helicopter pieces
class Target {
    int x;
    int y;
    Cell pivot;
    
    // the constructor
    Target(Cell pivot) {
        this.pivot = pivot;
        this.x = this.pivot.x;
        this.y = this.pivot.y;
    }
    
    // represent the helicopter pieces' images
    public WorldImage piecesImage() {
        return new RectangleImage(new Posn(this.x * 10 + 5, this.y * 10 + 5), 10, 10, new Black());
    }
    
    // check if the player's cell equal to the pieces' cell
    boolean picked(Cell player) {
        return (this.pivot.x == player.x) &&
                (this.pivot.y == player.y);
    }
}

// represent the helicopter 
class HelicopterTarget extends Target {

    HelicopterTarget(Cell pivot) {
        super(pivot);
    }
    // represent the helicopter image
    public WorldImage helicopterImage() {
        return new FromFileImage(new Posn(this.pivot.x * 10 + 5, this.pivot.y * 10 + 5),
                "helicopter.png");
    }
}


//Represents a single square of the game area
class Cell {
    // represents absolute height of this cell, in feet
    double height;
    // In logical coordinates, with the origin at the top-left corner of the
    // screen
    int x;
    int y;
    // the four adjacent cells to this one
    Cell left;
    Cell top;
    Cell right;
    Cell bottom;
    // reports whether this cell is flooded or not
    boolean isFlooded;
  
    // the constructor
    Cell(double height, int x, int y, boolean isFlooded) {
        this.height = height;
        this.x = x;
        this.y = y;
        this.left = null;
        this.top = null;
        this.right = null;
        this.bottom = null;
        this.isFlooded = isFlooded;
    }
    
    // modifies this cell's adjacent cell to refer to the given cells
    void updateCell(Cell left, Cell right, Cell top, Cell bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    // check if the cell is near water, if its left or right or top or bottom
    // is flooded, its isFlooded will become true
    public boolean nearWater() {
        return !this.isFlooded &&
                (this.left.isFlooded || this.top.isFlooded 
                        || this.right.isFlooded || this.bottom.isFlooded);
    }
    
    // flood cell if the cell is nearWater
    // change its isFlooded to true
    // also update its nearby cell
    public void updateIsFlood(int waterHeight) {
        if (this.height < waterHeight) {
            this.isFlooded = true;
            if (!this.left.isFlooded) {
                this.left.updateIsFlood(waterHeight);
            }
            if (!this.top.isFlooded) {
                this.top.updateIsFlood(waterHeight);
            }
            if (!this.right.isFlooded) {
                this.right.updateIsFlood(waterHeight);
            }
            if (!this.bottom.isFlooded) {
                this.bottom.updateIsFlood(waterHeight);
            }
        }
    }
  
    // to represent the image of each cell of the island
    public WorldImage cellImage(int waterHeight) {
        Color c;
        // the cell is not flooded
        if (!this.isFlooded) {
            // color: red between black, is not flooded & height +< water height
            if (this.height < waterHeight) {
                c = new Color((int) Math.min(125, 
                        255 - (waterHeight - this.height) * 8), 70 - waterHeight * 2, 50);
            }
            // color: green to white, is not flooded & height > waterHeight
            else {
                c = new Color((int) Math.min(255, (this.height - waterHeight) * 8),
                        (int) Math.min(255, 200 + (this.height - waterHeight) * 7),
                        (int) Math.min(255, (this.height - waterHeight) * 8));
            }

        }
        // the cell is flooded
        // color: blue to black
        else {
            c = new Color(0, 0, (int) (255 - (waterHeight - this.height) * 8));
            // (int) (255 - (waterHeight - this.height) * 5));
        }
        // each cell's size should be 10 * 10
        return new RectangleImage(new Posn(this.x * 10 + 5, this.y * 10 + 5), 10, 10, c);

    }
}

// represent a OceanCell
class OceanCell extends Cell {
    // constructor
    OceanCell(double height, int x, int y, boolean isFlooded) {
        super(height, x, y, isFlooded);
        this.isFlooded = true;
    }
  
    // to create the image of ocean cell
    public WorldImage cellImage(int waterHeight) {
        return new RectangleImage(new Posn(this.x * 10 + 5, this.y * 10 + 5), 10, 10,
                new Color(0, 0, 255));
    }
}

//Represents anything that can be iterated over
interface Iterable<T> {
//Returns an iterator over this collection
    Iterator<T> iterator();
}

// represent IListIterator to use iterator for ilist
class IListIterator<T> implements Iterator<T> {
    IList<T> items;
    // constructor
    IListIterator(IList<T> items) {
        this.items = items;
    }
    // Does this list have at least one more value?
    public boolean hasNext() {
        return this.items.isCons();
    }
     // Get the next value in this list
    // EFFECT: Advance the iterator to the subsequent value
    public T next() {
        if (this.hasNext()) {
            Cons<T> itemsasCons = this.items.asCons();
            T answer = itemsasCons.first;
            this.items = itemsasCons.rest;
            return answer;
        }
        else {
            throw new RuntimeException("There is no next item");
        }
    }
    
    // EFFECT: Remove the item just returned by next()
    // NOTE: This method may not be supported by every iterator; ignore it for
    // now
    public void remove() {
        throw new UnsupportedOperationException("Don't do this!");
    }
    
}



//represent a abstract list
interface IList<T> extends Iterable<T> {
    // add a new T to the IList<T>
    IList<T> add(T that);
    
    // check if the ilist is a cons list
    boolean isCons();
    
    // return the cons of the ilist
    Cons<T> asCons();
    
    // get a list's size
    int size();
    
    // remove one from a list
    IList<T> remove(T that);
    
   // if the piece has been picked, remove it from the list
    IList<T> pickedUp(Cell that);
    
    
}

//represent a abstract empty list
class Empty<T> implements IList<T> {

    // constructor
    Empty() {
     // nothing to do!
    }
 
    // iterator in the empty list<T>
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
 
    // add(T) in the empty list
    public IList<T> add(T that) {
        return new Cons<T>(that, this);
    }
 
    // empty is not a cons, return false
    public boolean isCons() {
        return false;
    }

    // empty is not a cons, return exception
    public Cons<T> asCons() {
        throw new ClassCastException("Empty is not a cons");
    }
    
    //size in the empty list
    public int size() {
        return 0;
    }
    
    // remove in the empty
    public IList<T> remove(T that) {
        return new Empty<T>();
    }
    
    // if the piece has been picked, remove it from the list
    // in the empty list
    public IList<T> pickedUp(Cell player) {
        return new Empty<T>();
    }

}

//represent a abstract cons list
class Cons<T> implements IList<T> {
    T first;
    IList<T> rest;

    // constructor
    Cons(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }
 
    // the iterator in the cons list<T>
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
 
    //add(T) in the cons list
    public IList<T> add(T that) {
        return new Cons<T>(that, this);
    }
 
    // check if cons is a cons list, return true
    public boolean isCons() {
        return true;
    }

   // return the cons type of the cons list
    public Cons<T> asCons() {
        return this;
    }
    
    // size in the cons list
    public int size() {
        return 1 + this.rest.size();
    }

    // remove in the cons list
    public IList<T> remove(T that) {
        if (that == this.first) {
            return this.rest;
        } 
        else {
            return new Cons<T>(this.first, this.rest.remove(that));
        }

    }
    
    // if the piece has been picked, remove it from the list
    // in the cons list
    public IList<T> pickedUp(Cell player) {
        if (((Target) this.first).picked(player)) {
            return this.remove(this.first);
        }
        else {
            return new Cons<T>(this.first, this.rest.pickedUp(player));
        }
    }
    
 
}

//represent the forbidden island world
class ForbiddenIslandWorld extends World {
    // All the cells of the game, including the ocean
    IList<Cell> board;
    // the current height of the ocean
    int waterHeight;
    // Defines an int constant
    static final int WORLD_SIZE = 640;
    static final int ISLAND_SIZE = 64;
    // the player
    Player aplayer;
    // the helicopter
    HelicopterTarget helicopter;
    // the pieces of the helicopter
    IList<Target> pieces;
 
    // the constructor of the world
    ForbiddenIslandWorld(int kind) {
        if (kind == 1) {
            this.board = this.cell2List(this.drawCell(this.drawMountain()));
            this.waterHeight = 0;
            this.aplayer = new Player(this.randomCell());
            this.pieces = this.threePieces();
            this.helicopter = new HelicopterTarget(this.highestCell());
        } 
        else if (kind == 2) {
            this.waterHeight = 0;
            this.board = this.cell2List(this.drawCell(this.drawDiamond()));
            this.aplayer = new Player(this.randomCell());
            this.pieces = this.threePieces();
            this.helicopter = new HelicopterTarget(this.highestCell());
        } 
        else if (kind == 3) {
            this.waterHeight = 0;
            this.board = this.cell2List(this.drawCell(this.drawTerrain()));
            this.aplayer = new Player(this.randomCell());
            this.pieces = this.threePieces();
            this.helicopter = new HelicopterTarget(this.highestCell());
        } 
        else {
            throw new IllegalArgumentException("There is no this type of mountain");

        }

    }
    
    // connect three pieces into a list of pieces with 3 elements
    IList<Target> threePieces() {
        IList<Target> pieces = new Empty<Target>();
        for (int index = 0; index < 3; index = index + 1) {
            pieces = pieces.add(new Target(this.randomCell()));
        }
        return pieces;
    }

    //convert ArrayList<ArrayList<Cell>> to IList<Cell>
    // and represent all the cells of the game
    IList<Cell> cell2List(ArrayList<ArrayList<Cell>> cell) {
        IList<Cell> result = new Empty<Cell>();
        for (int col = 0; col < ISLAND_SIZE; col = col + 1) {
            for (int row = 0; row < ISLAND_SIZE; row = row + 1) {
                result = result.add(cell.get(col).get(row));
            }   
        }
        return result;
    }

    // connect all arrc together
    public void connect(ArrayList<ArrayList<Cell>> arrc) {
        for (int a = 0; a < ISLAND_SIZE; a = a + 1) {
            for (int b = 0; b < ISLAND_SIZE; b = b + 1) {
                Cell thisCell = arrc.get(a).get(b);

                // fix the left link
                if (b > 0) {
                    thisCell.left = (arrc.get(a).get(b - 1));
                } 
                else {
                    thisCell.left = (thisCell);
                }
                // fix the right link
                if (b < ISLAND_SIZE - 1) {
                    thisCell.right = (arrc.get(a).get(b + 1));
                } 
                else {
                    thisCell.right = (thisCell);
                }
                // fix the top link
                if (a > 0) {
                    thisCell.top = (arrc.get(a - 1).get(b));
                } 
                else {
                    thisCell.top = (thisCell);
                }
                // fix the bottom link
                if (a < ISLAND_SIZE - 1) {
                    thisCell.bottom = (arrc.get(a + 1).get(b));
                } 
                else {
                    thisCell.bottom = (thisCell);
                }
            }
        }
    }

    // convert ArrayList<ArrayList<Double>> to ArrayList<ArrayList<Cell>>
    ArrayList<ArrayList<Cell>> drawCell(ArrayList<ArrayList<Double>> height) {
        ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>(ISLAND_SIZE);
        for (int col = 0; col < ISLAND_SIZE; col = col + 1) {
            result.add(new ArrayList<Cell>());
            for (int row = 0; row < ISLAND_SIZE; row = row + 1) {
                // when height is > 0, define as island cell, has green color
                if (height.get(col).get(row) > 0) {
                    result.get(col).add(new Cell(height.get(col).get(row).intValue(), 
                            row, col, false));
                }
                // when height <= 0, define as ocean cell, has blue color
                else {
                    result.get(col).add(new OceanCell(0, row, col, false));
                }
            }
        }
        this.connect(result);
        return result;
    }

    //represent normal type of mountain with ArrayList<ArrayList<Double>>
    ArrayList<ArrayList<Double>> drawMountain() {
        ArrayList<ArrayList<Double>> aar = 
             new ArrayList<ArrayList<Double>>(ISLAND_SIZE);
        Posn center = new Posn(ISLAND_SIZE / 2 - 1, ISLAND_SIZE / 2 - 1);
        // to create 64 column ArrayList<Double>()
        for (int col = 0; col < ISLAND_SIZE; col = col + 1) {
            aar.add(new ArrayList<Double>());
            // to add different height numbers in each column
            // height defined by island height - Manhattan distance
            for (int row = 0; row < ISLAND_SIZE; row = row + 1) {
                aar.get(col).add(31.0 - (Math.abs(center.x - row) 
                     + Math.abs(center.y - col)));
            }
        }    
        return aar;
    }

    // to represent a diamond island with ArrayList<ArrayList<Double>>
    ArrayList<ArrayList<Double>> drawDiamond() {
        ArrayList<ArrayList<Double>> aar = new ArrayList<ArrayList<Double>>(ISLAND_SIZE);
        int limit;
        Posn center = new Posn(ISLAND_SIZE / 2 - 1, ISLAND_SIZE / 2 - 1);
        // to create 64 column ArrayList<Double>()
        for (int col = 0; col < ISLAND_SIZE; col = col + 1) {
            aar.add(new ArrayList<Double>());
            // to add different height numbers in each column
            // height defined by random (island height - Manhattan distance)
            // the limit of random height is (0, 31)
            for (int row = 0; row < ISLAND_SIZE; row = row + 1) {
                limit = 31 - (Math.abs(center.x - row) + Math.abs(center.y - col));
                if (limit <= 0.0) {
                    aar.get(col).add(0.0);
                } 
                else {
                    aar.get(col).add((double) new Random().nextInt(31) + 1);
                }
            }
        }
        return aar;
    }

    // to represent a terrain island with ArrayListArrayList<Double>
    ArrayList<ArrayList<Double>> drawTerrain() {
        ArrayList<ArrayList<Double>> aar = new ArrayList<ArrayList<Double>>();
        for (int col = 0; col <= ISLAND_SIZE; col = col + 1) {
            aar.add(new ArrayList<Double>());
            for (int row = 0; row <= ISLAND_SIZE; row = row + 1) {
                aar.get(col).add(row, 0.0);
            }
        }

        aar.get(ISLAND_SIZE / 2).set(ISLAND_SIZE / 2, ISLAND_SIZE / 2.0);
        aar.get(ISLAND_SIZE / 2).set(0, 1.0);
        aar.get(ISLAND_SIZE / 2).set(ISLAND_SIZE, 1.0);
        aar.get(0).set(ISLAND_SIZE / 2, 1.0);
        aar.get(ISLAND_SIZE).set(ISLAND_SIZE / 2, 1.0);
        subdivision(aar, 0, ISLAND_SIZE / 2, 0, ISLAND_SIZE / 2);
        subdivision(aar, 0, ISLAND_SIZE / 2, ISLAND_SIZE / 2, ISLAND_SIZE);
        subdivision(aar, ISLAND_SIZE / 2, ISLAND_SIZE, 0, ISLAND_SIZE / 2);
        subdivision(aar, ISLAND_SIZE / 2, ISLAND_SIZE, ISLAND_SIZE / 2, ISLAND_SIZE);
        return aar;
    }

    // subdivision: divides the grid into quarters at each step
    // construct the height of the inside of a rectangle of cells,
    // given only the heights of the corners of the rectangle
    void subdivision(ArrayList<ArrayList<Double>> aar, int c1, int c64, int r1, int r64) {
        int s = c64 - c1;
        if ((s > 1) && ((r64 - r1) > 1)) {
            int c32 = (c1 + c64) / 2;
            int r32 = (r1 + r64) / 2;

            // set the height of four corners
            double tl = aar.get(c1).get(r1);
            double tr = aar.get(c64).get(r1);
            double bl = aar.get(c1).get(r64);
            double br = aar.get(c64).get(r64);
            // compute the random height of four mid points
            double t = (tl + tr) / 2 + (double) (new Random().nextDouble() - .8) * s / 2;
            double b = (bl + br) / 2 + (double) (new Random().nextDouble() - .8) * s / 2;
            double l = (tl + bl) / 2 + (double) (new Random().nextDouble() - .8) * s / 2;
            double r = (tr + br) / 2 + (double) (new Random().nextDouble() - .8) * s / 2;
            // set the new five heights to the midpoints
            aar.get(c32).set(r1, t);
            aar.get(c32).set(r64, b);
            aar.get(c1).set(r32, l);
            aar.get(c64).set(r32, r);
            double m = (tl + tr + bl + br) / 4 + (double) (new Random().nextDouble() - .8) * s / 2;
            aar.get(c32).set(r32, m);
            // apply the subdivision algorithm again
            // to the four parts
            subdivision(aar, c1, c32, r1, r32);
            subdivision(aar, c1, c32, r32, r64);
            subdivision(aar, c32, c64, r1, r32);
            subdivision(aar, c32, c64, r32, r64);
        }
    }
 
    // to create the image of all cell
    public WorldImage boardImage() {
        // make a blank boardImage first
        WorldImage boardImage = new RectangleImage(new Posn(0, 0), 0, 0, new White());
        // convert IList<Cell> to Iterator<Cell>
        Iterator<Cell> iterator = this.board.iterator();
        // check if the Iterator has next, overlay one by one
        // if not, return the whole image
        while (iterator.hasNext()) {
            Cell next = iterator.next();
            boardImage = boardImage.overlayImages(next.cellImage(waterHeight));
        }
        return boardImage;
    }
    
    // draw the target image
    public WorldImage targetImage() {
        WorldImage target = new RectangleImage(new Posn(0, 0), 0, 0, new White());
        Iterator<Target> iterator = this.pieces.iterator();
        while (iterator.hasNext()) {
            target = target.overlayImages(iterator.next().piecesImage());
        }
        target = target.overlayImages(this.helicopter.helicopterImage());
        return target;
    }

    // construct all cells near water
    public IList<Cell> nearWaterCell() {
        // make a empty list cell first
        IList<Cell> result = new Empty<Cell>();
        // convert IList<Cell> to Iterator<Cell>
        Iterator<Cell> iterator = this.board.iterator();
        while (iterator.hasNext()) {
            Cell next = iterator.next();
            if (next.nearWater()) {
                result = result.add(next);
            }
        }
        return result;
    }

    // Creates the canvas with every elements of the game
    // whole image of the game
    public WorldImage makeImage() {
        return this.boardImage().overlayImages(
                this.targetImage().overlayImages(this.aplayer.pilot()));
    }

    // random choose a cell from landCell
    public Cell randomCell() {
        IList<Cell> land = this.landCell();
        // pick a random number from a list of land cells' size
        int randomNumber = new Random().nextInt(land.size());
        // convert the land cells to iterator
        Iterator<Cell> iterator = land.iterator();
        Cell randomCell = iterator.next();
        while (randomNumber >= 0) {
            Cell thisCell = iterator.next();
            if (randomNumber == 0) {
                randomCell = thisCell;
            }
            randomNumber = randomNumber - 1;
        }
        return randomCell;

    }
    
    // choose the highest cell from landCell
    public Cell highestCell() {
        IList<Cell> land = this.landCell();
        // pick a random number from a list of land cells' size
        Iterator<Cell> iterator = land.iterator();
        Cell highestCell = iterator.next();
        while (iterator.hasNext()) {
            Cell thisCell = iterator.next();
            if (thisCell.height > highestCell.height) {
                highestCell = thisCell;
            }

        }
        return highestCell;
    }

    // construct all land cells
    public IList<Cell> landCell() {
        // make a empty list cell first
        IList<Cell> result = new Empty<Cell>();
        // convert IList<Cell> to Iterator<Cell>
        Iterator<Cell> iterator = this.board.iterator();
        while (iterator.hasNext()) {
            Cell next = iterator.next();
            if (!next.isFlooded) {
                result = result.add(next);
            }
        }
        return result;
    }

    // to flood all cells
    public void floodall() {
        // convert IList<Cell> to Iterator<Cell>
        // we construct all cell near water into an iterator
        Iterator<Cell> iterator = this.nearWaterCell().iterator();
        // check if the Iterator has next, if yes, do something! like:flood
        // if not, stop void
        while (iterator.hasNext()) {
            Cell next = iterator.next();
            next.updateIsFlood(waterHeight);
        }
    }

    // define onTick method, on each tick, add the water Height and flood the
    // cell near water
    public void onTick() {
        this.waterHeight = this.waterHeight + 1;
        this.floodall();
    }

    // end the world
    public WorldEnd worldEnds() {
        // when the player is flooded
        if (this.aplayer.pivot.isFlooded) {
            return new WorldEnd(true, 
                    new OverlayImages(new RectangleImage(new Posn(0, 0), 0, 0, new Black()),
                    new TextImage(new Posn(WORLD_SIZE / 2, WORLD_SIZE / 2), 
                            "YOU LOSE", 100, 100, new Red())));
        }
        // when the player get all pieces
        else if (this.aplayer.pivot == this.helicopter.pivot && !this.pieces.isCons()) {
            return new WorldEnd(true, 
                    new OverlayImages(new RectangleImage(new Posn(0, 0), 0, 0, new Black()),
                    new TextImage(new Posn(WORLD_SIZE / 2, WORLD_SIZE / 2), 
                            "YOU WIN", 100, 100, new Red())));

        } 
        else {
            return new WorldEnd(false, this.makeImage());
        }
    }

    // change the island with keyEvent input
    public void onKeyEvent(String k) {
        // press m key to create a new normal mountain
        if (k.equals("m")) {
            // change the waterHeight to see different color of cell
            // whose height is under waterHeight
            // in the normal mountain island
            // the biggest waterHeight should be 31
            // return new
            // ForbiddenIslandWorld(this.cell2List(this.drawCell(this.drawMountain()),
            // 31);
            this.board = this.cell2List(this.drawCell(this.drawMountain()));
            this.waterHeight = 0;
            this.aplayer = new Player(this.randomCell());
            this.pieces = this.threePieces();
            this.helicopter = new HelicopterTarget(this.highestCell());

        }
        // press d key to create a new diamond mountain
        else if (k.equals("r")) {
            // change the waterHeight to see different color of cell
            // whose height is under waterHeight
            // in the normal mountain island
            // the biggest waterHeight should be 31
            this.waterHeight = 0;
            this.board = this.cell2List(this.drawCell(this.drawDiamond()));
            this.aplayer = new Player(this.randomCell());
            this.pieces = this.threePieces();
            this.helicopter = new HelicopterTarget(this.highestCell());
        }
        // press t key to create a new terrain island
        else if (k.equals("t")) {
            this.waterHeight = 0;
            this.board = this.cell2List(this.drawCell(this.drawTerrain()));
            this.aplayer = new Player(this.randomCell());
            this.pieces = this.threePieces();
            this.helicopter = new HelicopterTarget(this.highestCell());
        }

        // press left key
        if (k.equals("left") && !this.aplayer.pivot.left.isFlooded) {
            this.aplayer.updatePlayer(this.aplayer.pivot.left);
            this.pieces = this.pieces.pickedUp(this.aplayer.pivot);
        }
        // press right key
        if (k.equals("right") && !this.aplayer.pivot.right.isFlooded) {
            this.aplayer.updatePlayer(this.aplayer.pivot.right);
            this.pieces = this.pieces.pickedUp(this.aplayer.pivot);
        }
        // press up key
        if (k.equals("up") && !this.aplayer.pivot.top.isFlooded) {
            this.aplayer.updatePlayer(this.aplayer.pivot.top);
            this.pieces = this.pieces.pickedUp(this.aplayer.pivot);
        }
        // press down key
        if (k.equals("down") && !this.aplayer.pivot.bottom.isFlooded) {
            this.aplayer.updatePlayer(this.aplayer.pivot.bottom);
            this.pieces = this.pieces.pickedUp(this.aplayer.pivot);
        } 
        else {
            return;
        }

    }

}

// the examples of the world
class ExamplesIsland {

    IList<Integer> mt = new Empty<Integer>();
    IList<Integer> lon1 = new Cons<Integer>(1, this.mt);
    IList<Integer> lon21 = new Cons<Integer>(2, this.lon1);
    IList<Integer> lon321 = new Cons<Integer>(3, this.lon21);

    Cons<Integer> con1 = new Cons<Integer>(1, this.mt);
    Cons<Integer> con21 = new Cons<Integer>(2, this.lon1);
    Cons<Integer> con321 = new Cons<Integer>(3, this.lon21);

    IListIterator<Integer> itemt = new IListIterator<Integer>(this.mt);
    IListIterator<Integer> ite1 = new IListIterator<Integer>(this.lon1);
    IListIterator<Integer> ite321 = new IListIterator<Integer>(this.lon321);

    // test the method isCons()
    boolean testIsCons(Tester t) {
        return t.checkExpect(this.lon321.isCons(), true) && t.checkExpect(this.lon1.isCons(), true)
                && t.checkExpect(this.mt.isCons(), false);
    }

    // test the method hasNext()
    boolean testHasNext(Tester t) {
        return t.checkExpect(this.ite321.hasNext(), true) 
                && t.checkExpect(this.ite1.hasNext(), true)
                && t.checkExpect(this.itemt.hasNext(), false);
    }

    // test the method next()
    boolean testNext(Tester t) {
        return t.checkExpect(this.ite321.next(), 3) && t.checkExpect(this.ite1.next(), 1)
                && t.checkException(new RuntimeException("There is no next item"), 
                        this.itemt, "next");
    }

    // test the method add(T)
    boolean testAdd(Tester t) {
        return t.checkExpect(this.mt.add(1), this.lon1) 
                && t.checkExpect(this.lon1.add(2), this.lon21)
                && t.checkExpect(this.lon21.add(3), this.lon321);
    }
    
    // test the method size()
    boolean testSize(Tester t) {
        return t.checkExpect(this.mt.size(), 0)
                && t.checkExpect(this.lon1.size(), 1)
                && t.checkExpect(this.lon21.size(), 2);
        
    }
    
    // test the method remve
    boolean testRemove(Tester t) {
        return t.checkExpect(this.mt.remove(1), this.mt)
                && t.checkExpect(this.lon1.remove(1), this.mt)
                && t.checkExpect(this.lon21.remove(2), this.lon1);
    }
    

    // test the method asCons()
    boolean testAsCons(Tester t) {
        return t.checkExpect(this.lon1.asCons(), this.con1) 
                && t.checkExpect(this.lon21.asCons(), this.con21)
                && t.checkExpect(this.lon321.asCons(), this.con321)
                && t.checkException(new ClassCastException("Empty is not a cons"), 
                        this.mt, "asCons");
    }

    OceanCell oceancell1 = new OceanCell(15, 10, 20, true);
    OceanCell oceancell2 = new OceanCell(30, 5, 5, true);
    OceanCell oceancell3 = new OceanCell(5, 7, 7, true);

    // test the method cellImage
    boolean testCellImage(Tester t) {
        return t.checkExpect(this.oceancell1.cellImage(10),
                new RectangleImage(new Posn(105, 205), 10, 10, new Color(0, 0, 255)))
                && t.checkExpect(this.oceancell2.cellImage(20),
                        new RectangleImage(new Posn(55, 55), 10, 10, new Color(0, 0, 255)))
                && t.checkExpect(this.oceancell3.cellImage(30),
                        new RectangleImage(new Posn(75, 75), 10, 10, new Color(0, 0, 255)))
                && t.checkExpect(this.cell4.cellImage(10),
                        new RectangleImage(new Posn(15, 15), 10, 10, new Color(125, 50, 50)));
    }

    Cell cell1 = new Cell(1.0, 0, 1, false);
    Cell cell2 = new Cell(1.0, 2, 1, false);
    Cell cell3 = new Cell(2.0, 1, 2, false);
    Cell cell4 = new Cell(1.0, 1, 1, false);
    Cell cell5 = new Cell(0.0, 1, 0, false);
    Cell cell6 = new Cell(0.0, 0, 2, true);
    Player p = new Player(this.cell1);
    Target t = new Target(this.cell2);
    HelicopterTarget h = new HelicopterTarget(this.cell3);
    IList<Target> lot1 = new Cons<Target>(this.t, new Empty<Target>());

    IList<Cell> empty = new Empty<Cell>();
    IList<Cell> loc1 = new Cons<Cell>(this.cell1, this.empty);
    IList<Cell> loc2 = new Cons<Cell>(this.cell2, this.loc1);
    IList<Cell> loc3 = new Cons<Cell>(this.cell3, this.loc2);
    IList<Cell> loc4 = new Cons<Cell>(this.cell4, this.loc3);
    IList<Cell> loc5 = new Cons<Cell>(this.cell5, this.loc4);

    // test updateCells
    void initTestConditions() {
        this.cell1 = new Cell(1.0, 0, 1, false);
        this.cell2 = new Cell(1.0, 2, 1, false);
        this.cell3 = new Cell(2.0, 1, 2, false);
        this.cell4 = new Cell(1.0, 1, 1, false);
        this.cell5 = new Cell(0.0, 1, 0, false);
        this.cell6 = new Cell(0.0, 0, 2, true);
        this.p = new Player(this.cell1);
        this.empty = new Empty<Cell>();
        this.loc1 = new Cons<Cell>(this.cell1, this.empty);
        this.loc2 = new Cons<Cell>(this.cell2, this.loc1);
        this.loc3 = new Cons<Cell>(this.cell3, this.loc2);
        this.loc4 = new Cons<Cell>(this.cell4, this.loc3);
        this.loc5 = new Cons<Cell>(this.cell5, this.loc4);
    }

    // test the method updateCell
    void testUpdateCell(Tester t) {
        this.initTestConditions();
        this.cell4.updateCell(this.cell1, this.cell5, this.cell2, this.cell3);
        t.checkExpect(this.cell4.left, this.cell1);
        t.checkExpect(this.cell4.right, this.cell5);
        t.checkExpect(this.cell4.top, this.cell2);
        t.checkExpect(this.cell4.bottom, this.cell3);

    }
    
    // test the method updatePlayer
    void testUpadtePlayer(Tester t) {
        this.initTestConditions();
        this.p.updatePlayer(this.cell4);
        t.checkExpect(this.p.pivot, this.cell4);
        t.checkExpect(this.p.x, 1);
        t.checkExpect(this.p.y, 1);
    }

    // test the method picked
    void testPicked(Tester t) {
        this.initTestConditions();
        t.checkExpect(this.t.picked(cell1), false);
        this.p.updatePlayer(this.cell2);
        t.checkExpect(this.t.picked(this.p.pivot), true);
    }
    
// not able to write test
//    // test the method pickedup
//    void testPickedUp(Tester t) {
//        this.initTestConditions();
//        this.p.updatePlayer(this.cell5);
//        this.loc5.pickedUp(this.cell5);
//        t.checkExpect(this.loc5.pickedUp(this.cell5), this.loc4);
//    }

    // test the method helicopter image
    boolean testHelicopterImage(Tester t) {
        return t.checkExpect(this.h.helicopterImage(), new FromFileImage(new Posn(15, 25), 
                "helicopter.png"));

    }

    // test the method nearWater
    void testNearWater(Tester t) {
        initTestConditions();
        this.cell4.updateCell(this.cell1, this.cell5, this.cell2, this.cell3);
        t.checkExpect(this.cell4.nearWater(), false);
        this.cell4.updateCell(this.cell1, this.cell5, this.cell2, this.cell6);
        t.checkExpect(this.cell4.left, this.cell1);
        t.checkExpect(this.cell4.right, this.cell5);
        t.checkExpect(this.cell4.top, this.cell2);
        t.checkExpect(this.cell4.bottom, this.cell6);
        t.checkExpect(this.cell4.bottom.isFlooded, true);
        t.checkExpect(this.cell4.nearWater(), true);
    }

    // test method updateIsFlood
    // by the method floodall
    void testUpdateIsFlood(Tester t) {
        this.initTestConditions();
        initData();
        this.cell4.updateCell(this.cell1, this.cell5, this.cell2, this.cell3);
        this.w.floodall();
        // t.checkExpect(this.cell4.isFlooded, true);
        // t.checkExpect(this.cell4, new Cell(1.0, 1, 1, true));
    }
    

    // the initial List
    ArrayList<Cell> alist = new ArrayList<Cell>();
    ArrayList<Double> blist = new ArrayList<Double>();
    ArrayList<ArrayList<Cell>> clist = new ArrayList<ArrayList<Cell>>();
    ArrayList<ArrayList<Double>> dlist = new ArrayList<ArrayList<Double>>();

    void reset() {
        this.alist.add(cell1);
        this.blist.add(1.0);
        this.clist.add(this.alist);
        this.dlist.add(this.blist);
    }

    /*
     * these are what I want to check, but I am not able to:
     * 
     * boolean testThreePieces(Tester t) { return
     * t.checkExpect(this.w.threePieces(), lot1); }
     * 
     * boolean testCell2List(Tester t) { return
     * t.checkExpect(this.w.cell2List(this.clist), new Cons<Cell>(this.cell1,
     * new Empty<Cell>())); }
     */
    /*
     * void testConnect(Tester t) {
     * 
     * }
     */

    /*
     * boolean testDrawCell(Tester t) {
     * 
     * }
     * 
     * boolean testDrawMountain(Tester t) {
     * 
     * }
     * 
     * boolean testDrawDiamond(Tester t) {
     * 
     * }
     * 
     * boolean testDrawTettain(Tester t) {
     * 
     * }
     * 
     * void testSubdivision(Tester t) {
     * 
     * }
     */

    /*
     * boolean testtargetImage(Tester t) { return t.checkExpect(w.targetImage(),
     * this.lot1.overlayImages(this.helicopter }
     * 
     * boolean testmakeImage(Tester t) {
     * 
     * }
     * 
     * boolean testNearWaterCell(Tester t) {
     * 
     * }
     * 
     * boolean testRandomCell(Tester t) {
     * 
     * }
     * 
     * boolean testhighestCell(Tester t) {
     * 
     * }
     * 
     * boolean testlandcell(Tester t) {
     * 
     * }
     * 
     * void testFloodAll(Tester t) {
     * 
     * }
     * 
     * 
     * 
     */
    // check the boardImage of the world
    boolean boardImage(Tester t) {
        return t.checkExpect(w.boardImage(), new RectangleImage(new Posn(0, 0), 0, 0, new White()));
    }

    ForbiddenIslandWorld w;

    void initData() {
        w = new ForbiddenIslandWorld(1);
    }

    // test the method, onKeyEvent
    void testOnKeyEvent(Tester t) {
        initData();
        t.checkExpect(w.waterHeight, 0);
        w.onKeyEvent("d");
        t.checkExpect(w.waterHeight, 0);
        w.onKeyEvent("m");
        t.checkExpect(w.waterHeight, 0);
    }

    // to run the game
    void testAnimation(Tester t) {
        this.initData();
        // this.w.bigBang(ForbiddenIslandWorld.WORLD_SIZE,
        // ForbiddenIslandWorld.WORLD_SIZE, .8);
    }

}

