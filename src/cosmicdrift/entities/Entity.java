package cosmicdrift.entities;

import cosmicdrift.World;
import cosmicdrift.tiles.Tile;

public abstract class Entity {

    public final World world;
    public int x1, y1, x2, y2;
    public int vX, vY;

    public Entity(int x, int y, int width, int height, World world) {
        this.x1 = x;
        this.y1 = y;
        this.x2 = x + width;
        this.y2 = y + height;
        this.world = world;
    }

    public boolean tick() {
        if (vX != 0 || vY != 0) {
            this.move(vX, vY);
            int width = (x2 - x1) / 2;
            int height = (y2 - y1) / 2;
            int centerX = x1 + width;
            int centerY = y1 + height;
            if (!isColliding(centerX, centerY, width, height)) {
                for (int[] i : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                    if (isColliding(i[0] + centerX, i[1] + centerY, width, height)) {
                        if (i[0] != 0) {
                            vX = 0;
                        } else {
                            vY = 0;
                        }
                    }
                }
            }
        }
        vX = (int) (0.9 * vX);
        vY = (int) (0.9 * vY);
        return false;
    }

    public boolean hasStep(int rx, int ry) {
        int width = (x2 - x1) / 2;
        int height = (y2 - y1) / 2;
        int centerX = x1 + width;
        int centerY = y1 + height;
        return isColliding(centerX + rx, centerY + ry, width, height);
    }

    private void move(int velHoriz, int velVert) {
        int width = (x2 - x1) / 2;
        int height = (y2 - y1) / 2;
        int centerX = x1 + width;
        int centerY = y1 + height;
        if (velHoriz > 0) {
            int x;
            for (x = velHoriz; x > 0; x--) {
                if (!isColliding(centerX + x, centerY, width, height)) {
                    break;
                }
            }
            centerX += x;
        } else if (velHoriz < 0) {
            int x;
            for (x = velHoriz; x < 0; x++) {
                if (!isColliding(centerX + x, centerY, width, height)) {
                    break;
                }
            }
            centerX += x;
        }
        if (velVert > 0) {
            int y;
            for (y = velVert; y > 0; y--) {
                if (!isColliding(centerX, centerY + y, width, height)) {
                    break;
                }
            }
            centerY += y;
        } else if (velVert < 0) {
            int y;
            for (y = velVert; y < 0; y++) {
                if (!isColliding(centerX, centerY + y, width, height)) {
                    break;
                }
            }
            centerY += y;
        }
        x1 = centerX - width;
        y1 = centerY - height;
        x2 = x1 + 2 * width;
        y2 = y1 + 2 * height;
    }

    private boolean isColliding(int x, int y, int width, int height) {
        int x1 = x - width, x2 = x + width, y1 = y - height, y2 = y + height;
        int bx1 = x1 / Tile.TILE_SIZE, by1 = y1 / Tile.TILE_SIZE;
        int bx2 = x2 / Tile.TILE_SIZE, by2 = y2 / Tile.TILE_SIZE;
        for (int bx = bx1; bx <= bx2; bx++) {
            for (int by = by1; by <= by2; by++) {
                Tile t = world.getTile(bx, by);
                if (t == null || !world.isSolid(bx, by)) {
                    continue;
                }
                int tx1 = bx * Tile.TILE_SIZE, tx2 = bx * Tile.TILE_SIZE + Tile.TILE_SIZE;
                int ty1 = by * Tile.TILE_SIZE, ty2 = by * Tile.TILE_SIZE + Tile.TILE_SIZE;
                if (tx1 < x2 && x1 < tx2 && ty1 < y2 && y1 < ty2) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getIcon() {
        return null;
    }
}
