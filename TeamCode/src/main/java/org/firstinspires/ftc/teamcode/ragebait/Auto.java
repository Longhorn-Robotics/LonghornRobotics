package org.firstinspires.ftc.teamcode.ragebait; 
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name = "SampleAuto", group = "Examples")
public class Auto extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;
  
//Start positions which we will refine in testing:
    private final Pose bottom_launchzoneBLUE = new Pose(60, 8.4375, Math.toRadians(90)); // Bottom launch zone on the blue side (a,b,c)
    private final Pose bottom_launchzoneRED = new Pose(90, 8.4375, Math.toRadians(90)); // Bottom launch zone ON THE RED SIDE
    private final Pose blue_depot = new Pose(13.375,110 Math.toRadians(90)); // Blue depot
    private final Pose red_depot = new Pose(130.625,110 Math.toRadians(90)); // Red depot

//Ball Positions:
    private final Pose loadingzone_blue = new Pose(134.982, 6.481, Math.toRadians(180)); // The blue loading zone. Robot's back is facing towards the balls to pick them up.
    private final Pose loadingzone_red = new Pose(10, 6.481, Math.toRadians(360)); // The red loading zone. Robot's back is facing towards the balls to pick them up.
   // private final Pose  basezone_blue = new Pose()

//Launch Zones:
    //angles need to be refined
    private final Pose bottom_launchzoneBLUE = new Pose(60, 8.4375, Math.toRadians(50)); 
    private final Pose bottom_launchzoneRED = new Pose(90, 8.4375, Math.toRadians(122)); 
    private final Pose toplaunchzoneRED = new Pose(72, 104, Math.toRadians(40)); // midle of the big lauch zone
    private final Pose toplaunchzoneBLUE = new Pose(72, 104, Math.toRadians(145)); // midle of the big lauch zone TO THE BLUE GOAL

    private final Pose loadingzone_red = new pose(x,y, Math.toRadians()); //




private Path scorePreload;
private PathChain grabPickup1, scorePickup1, grabPickup2, scorePickup2, grabPickup3, scorePickup3;

//movement, also needs to be tuned and determine paths
//BezierLines are fine


public void buildPaths() {
    scorePreload = new Path(new BezierLine(startPose, scorePose));
    scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());

    
    grabPickup1 = follower.pathBuilder()
            .addPath(new BezierLine(scorePose, pickup1Pose))
            .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
            .build();

    scorePickup1 = follower.pathBuilder()
            .addPath(new BezierLine(pickup1Pose, scorePose))
            .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
            .build();
}


//managing the paths, needs work
public void autonomousPathUpdate() {
    switch (pathState) {
        case 0:
            follower.followPath(scorePreload);
            setPathState(1);
            break;
        case 1:

            /* You could check for
            - Follower State: "if(!follower.isBusy()) {}"
            - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
            - Robot Position: "if(follower.getPose().getX() > 36) {}"
            */

            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
            if(!follower.isBusy()) {
                /* Score Preload */

                /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                follower.followPath(grabPickup1,true);
                setPathState(2);
            }
            break;
        case 2:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup1Pose's position */
            if(!follower.isBusy()) {
                /* Grab Sample */

                /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                follower.followPath(scorePickup1,true);
                setPathState(3);
            }
            break;
        case 3:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
            if(!follower.isBusy()) {
                /* Score Sample */

                /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                follower.followPath(grabPickup2,true);
                setPathState(4);
            }
            break;
        case 4:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup2Pose's position */
            if(!follower.isBusy()) {
                /* Grab Sample */

                /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                follower.followPath(scorePickup2,true);
                setPathState(5);
            }
            break;
        case 5:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
            if(!follower.isBusy()) {
                /* Score Sample */

                /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                follower.followPath(grabPickup3,true);
                setPathState(6);
            }
            break;
        case 6:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup3Pose's position */
            if(!follower.isBusy()) {
                /* Grab Sample */

                /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
                follower.followPath(scorePickup3, true);
                setPathState(7);
            }
            break;
        case 7:
            /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
            if(!follower.isBusy()) {
                /* Set the state to a Case we won't use or define, so it just stops running an new paths */
                setPathState(-1);
            }
            break;
    }
}

/** These change the states of the paths and actions. It will also reset the timers of the individual switches **/
public void setPathState(int pState) {
    pathState = pState;
    pathTimer.resetTimer();
}
//main loop, will run after clicking play
    @Override
    public void loop() {

        follower.update();
        autonomousPathUpdate();
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();


        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);

    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop() {}

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void stop() {}

}
