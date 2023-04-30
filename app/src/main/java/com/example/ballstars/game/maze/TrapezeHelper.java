package com.example.ballstars.game.maze;

import static com.example.ballstars.game.Ball.RADIUS;
import static com.example.ballstars.game.maze.MazeSurfaceView.LENGTH;
import static com.example.ballstars.game.maze.MazeSurfaceView.SPACING;
import static com.example.ballstars.game.maze.MazeSurfaceView.TICK;
import android.graphics.Path;
import android.graphics.RectF;

public class TrapezeHelper {
    private final FPoint point1;
    private final FPoint point2;
    private final FPoint tempPoint;
    private final RectF allowedRectF;
    private Path allowedPath;
    public static float yPortion;
    public float newSpacing = SPACING-(2f*RADIUS);
    public TrapezeHelper(FPoint point1, FPoint point2) {
        this.point1 = point1;
        this.point2 = point2;


        float dy = Math.abs(point1.y - point2.y);
        yPortion = dy / (LENGTH/TICK);
        if (isUp()) {
            tempPoint = new FPoint(point2.x, point1.y - yPortion);
        } else {
            tempPoint = new FPoint(point2.x, point1.y + yPortion);
        }

        allowedPath = new Path();

        allowedPath.moveTo(point1.x, point1.y + newSpacing);
        allowedPath.lineTo(tempPoint.x, tempPoint.y + newSpacing);
        allowedPath.lineTo(tempPoint.x, tempPoint.y - SPACING);
        allowedPath.lineTo(point1.x, point1.y - SPACING);
        allowedPath.close();


        allowedRectF = new RectF();
        allowedPath.computeBounds(allowedRectF, true);

    }
    public void advanceTemp() {
        if (isUp()) {
            tempPoint.y -= yPortion;
        } else {
            tempPoint.y += yPortion;
        }
    }

    public void updateRectF() {
        allowedPath = new Path();

        if (isDone()) {
            allowedPath.moveTo(point1.x, point1.y + newSpacing);
            allowedPath.lineTo(point2.x, point2.y + newSpacing);
            allowedPath.lineTo(point2.x, point2.y - SPACING);
            allowedPath.lineTo(point1.x, point1.y - SPACING);
        } else {
            allowedPath.moveTo(point1.x, point1.y + newSpacing);
            allowedPath.lineTo(tempPoint.x, tempPoint.y + newSpacing);
            allowedPath.lineTo(tempPoint.x, tempPoint.y - SPACING);
            allowedPath.lineTo(point1.x, point1.y - SPACING);

        }
        allowedPath.close();
        allowedPath.computeBounds(allowedRectF, false);

    }
    public FPoint getPoint1() {
        return point1;
    }

    public FPoint getPoint2() {
        return point2;
    }

    public FPoint getTempPoint() {
        return tempPoint;
    }

    public Path getAllowedPath() {
        return allowedPath;
    }

    public RectF getAllowedRectF() {
        return allowedRectF;
    }

    public boolean isDone() {
        return point1.x <= point2.x - LENGTH;
    }

    public boolean isUp() {
        return point1.y > point2.y;
    }

}
