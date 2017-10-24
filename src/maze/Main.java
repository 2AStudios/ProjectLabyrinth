package maze;

import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class Main extends Application {

    Button btnscene2d,btnscene3d;
    Label lblscene2d;
    FlowPane panel2d;
    Scene sceneMain,scene2d, scene3d;
    Stage mainstage;
    Label moveCounter;
    //3D Scene
    final Group root3d = new Group();
    final Group root2d = new Group();
    final Xform axisGroup = new Xform();
    final Xform mazeGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -5;
    private static final double CAMERA_INITIAL_X_ANGLE = 0.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 90.0;
    private static final double CAMERA_INITIAL_Z_ANGLE = 0.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;

    private static String fileText = Main.readFile("res/maze16.txt");
    private static String[] indexList = fileText.split("\r\n");
    private static char[][] mazeList = new char[indexList[0].length()][indexList.length];
    private static PhongMaterial wallMaterial= new PhongMaterial();
    private static int characterX = 0;
    private static int characterY = 0;
    private static int characterR = 0;
    private static int moveCount = 0;
    private static boolean resetGame = false;
    private static boolean inAnimation = false;


    /*private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 5.0;
    private static final double TRACK_SPEED = 0.3;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;*/

    //   private void buildScene() {
    //       root3d.getChildren().add(world);
    //   }
    private void buildCamera() {
        System.out.println("buildCamera()");
        root3d.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        for(int i = 0;i<indexList[0].length();i++){
            for(int j = 0;j<indexList.length;j++){
                if(mazeList[i][j] == '^'){
                    characterX = j;
                    characterY = i;
                    camera.setTranslateZ(i*10);
                    camera.setTranslateX(j*10);
                }
            }
        }
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setFieldOfView(60);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_Z_ANGLE);
    }

    /*private void handleMouse(Scene scene, final Node root3d) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 1.0;

                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                }
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                    camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);
                }
            }
        });
    }*/

    private void handleKeyboard(Scene scene, final Node root) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(!inAnimation && !resetGame)
                switch (event.getCode()) {
                    case W:
                        if(mazeList[characterY+1][characterX] == '$') {
                            System.out.println("Win Maze.");
                            resetGame = true;
                            if(moveCount < (mazeList.length*mazeList[0].length)/3)
                                moveCounter.setText("Great Job!\nMoves: " + moveCount);
                            else
                                moveCounter.setText("Great Perseverance!\nMoves: " + moveCount);
                        }
                        switch (characterR){
                            case 0: if(mazeList[characterY+1][characterX] == ' ') translateByY(10,camera); break;
                            case 90: if(mazeList[characterY][characterX+1] == ' ') translateByX(10,camera); break;
                            case 180: if(mazeList[characterY-1][characterX] == ' ') translateByY(-10,camera); break;
                            case 270: if(mazeList[characterY][characterX-1] == ' ') translateByX(-10,camera); break;
                        }
                        break;
                    case A:
                        rotateCamera(-90,camera);
                        break;
                    case S:
                        switch (characterR){
                            case 0: if(mazeList[characterY-1][characterX] == ' ') translateByY(-10,camera); break;
                            case 90: if(mazeList[characterY][characterX-1] == ' ') translateByX(-10,camera); break;
                            case 180: if(mazeList[characterY+1][characterX] == ' ') translateByY(10,camera); break;
                            case 270: if(mazeList[characterY][characterX+1] == ' ') translateByX(10,camera); break;
                        }
                        break;
                    case D:
                        rotateCamera(90,camera);
                        break;
                    case B:{
                        final PhongMaterial bcMaterial = new PhongMaterial();
                        bcMaterial.setDiffuseColor(Color.SANDYBROWN);
                        bcMaterial.setSpecularColor(Color.SANDYBROWN);
                        Box mazeCube = new Box(.2, .2, .2);
                        mazeCube.setMaterial(bcMaterial);
                        mazeCube.setTranslateX(characterY * 10);
                        mazeCube.setTranslateY(-4.9);
                        mazeCube.setTranslateZ(characterX * 10);
                        mazeGroup.getChildren().add(mazeCube);
                    }break;

                }
                else{
                    if(event.getCode() == KeyCode.SPACE) {
                        System.exit(0);
                    }
                }
            }
        });
    }

    private void rotateCamera(double r, PerspectiveCamera camera){
        inAnimation = true;
        RotateTransition rt = new RotateTransition(Duration.millis(300), camera);
        rt.setAxis(Rotate.Y_AXIS);
        rt.setByAngle(r);
        rt.setCycleCount(1);
        rt.setOnFinished(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent AE) {
                 inAnimation = false;
             }
        });
        rt.play();
        characterR+=r;
        if(characterR == 360) characterR = 0;
        if(characterR == -90) characterR = 270;
    }

    private void translateByX(double x, PerspectiveCamera camera){
        inAnimation = true;
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), camera);
        tt.setByX(x);
        tt.setCycleCount(1);
        tt.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent AE) {
                inAnimation = false;
            }
        });
        tt.play();
        if(x > 0)
            characterX++;
        else
            characterX--;
        moveCount++;
        moveCounter.setText("Moves: " + moveCount);
    }
    private void translateByY(double y, PerspectiveCamera camera){
        inAnimation = true;
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), camera);
        tt.setByZ(y);
        tt.setCycleCount(1);
        tt.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent AE) {
                inAnimation = false;
            }
        });
        tt.play();
        if(y > 0)
            characterY++;
        else
            characterY--;
        moveCount++;
        moveCounter.setText("Moves: " + moveCount);
    }

    private void buildMaze() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        wallMaterial = new PhongMaterial();
        Image diffuseWallMap = new Image(new File("res/Cobblestone_1024_albedo.png").toURI().toString());
        Image normalWallMap = new Image(new File("res/Cobblestone_1024_normal.png").toURI().toString());
        Image roughnessWallMap = new Image(new File("res/Cobblestone_1024_roughness.png").toURI().toString());

        wallMaterial.setDiffuseMap(diffuseWallMap);
        wallMaterial.setBumpMap(normalWallMap);
        wallMaterial.setSpecularMap(roughnessWallMap);
        wallMaterial.setSpecularColor(Color.SANDYBROWN);
        //wallMaterial.setSpecularPower(32);

        PhongMaterial floorMaterial = new PhongMaterial();
        Image diffuseFloorMap = new Image(new File("res/Cobblestone5_1024_albedo.png").toURI().toString());
        Image normalFloorMap = new Image(new File("res/Cobblestone5_1024_normal.png").toURI().toString());

        floorMaterial.setDiffuseMap(diffuseFloorMap);
        floorMaterial.setBumpMap(normalFloorMap);
        floorMaterial.setSpecularColor(Color.WHITE);

        Xform mazeXform = new Xform();

        for(int i = 0;i<indexList[0].length();i++){
            for(int j = 0;j<indexList.length;j++){
                if(mazeList[i][j] == '*') {
                    Box mazeCube = new Box(10, 10, 10);
                    mazeCube.setMaterial(wallMaterial);
                    mazeCube.setTranslateX(i * 10);
                    mazeCube.setTranslateZ(j * 10);
                    mazeXform.getChildren().add(mazeCube);
                }else{
                    Box mazeCube = new Box(10, 2, 10);
                    mazeCube.setMaterial(floorMaterial);
                    mazeCube.setTranslateX(i * 10);
                    mazeCube.setTranslateY(-6);
                    mazeCube.setTranslateZ(j * 10);
                    mazeXform.getChildren().add(mazeCube);
                }
                if(mazeList[i][j] == '$') {
                    Sphere mazeGoal = new Sphere(1);
                    mazeGoal.setMaterial(redMaterial);
                    mazeGoal.setTranslateX(i * 10);
                    mazeGoal.setTranslateY(1);
                    mazeGoal.setTranslateZ(j * 10);
                    mazeXform.getChildren().add(mazeGoal);
                }
            }
        }

        mazeGroup.getChildren().add(mazeXform);

        world.getChildren().addAll(mazeGroup);
    }

    public void buildMaze2d(){
        int blocksize = (int)(768/indexList.length)-1;
        Canvas canvas = new Canvas( indexList[0].length()*blocksize, indexList.length*blocksize );
        //System.out.println(Arrays.deepToString(indexList));
        System.out.println(indexList[0].length());
        System.out.println(indexList.length);
        root2d.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image brick = new Image(new File("res/prock.png").toURI().toString());
        Image cobblestone = new Image(new File("res/pground.png").toURI().toString());
        Image[] playerSprite = new Image[4];
        playerSprite[0] = new Image(new File("res/player0.png").toURI().toString());
        playerSprite[1] = new Image(new File("res/player1.png").toURI().toString());
        playerSprite[2] = new Image(new File("res/player2.png").toURI().toString());
        playerSprite[3] = new Image(new File("res/player3.png").toURI().toString());

        final long startNanoTime = System.nanoTime();

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                // background image clears canvas
                for(int i = 0;i<indexList[0].length();i++){
                    for(int j = 0;j<indexList.length;j++){
                        if(mazeList[i][j] == '*') {
                            gc.drawImage(brick,i*blocksize,j*blocksize,blocksize,blocksize);
                        }else{
                            gc.drawImage(cobblestone,i*blocksize,j*blocksize,blocksize,blocksize);
                        }
                        if(mazeList[i][j] == '$') {
                            gc.setFill(Color.RED);
                            gc.fillOval(i*blocksize, j*blocksize, blocksize, blocksize);
                        }
                }
                }
                gc.setFont(Font.font("Droid Sans", FontWeight.BOLD, 15));
                if(resetGame) {
                    //System.out.println("Win Maze.");
                    resetGame = true;
                    if(moveCount < (mazeList.length*mazeList[0].length)/3)
                        gc.fillText("Great Job!\nMoves: " + moveCount,5,20);
                    else
                        gc.fillText("Great Perseverance!\nMoves: " + moveCount,5,20);
                }else{
                    gc.fillText("Moves: "+moveCount,5,20);
                }

                switch (characterR){
                    case 0: gc.drawImage(playerSprite[0],characterY*blocksize,characterX*blocksize-25); break;
                    case 90: gc.drawImage(playerSprite[3],characterY*blocksize,characterX*blocksize-25); break;
                    case 180: gc.drawImage(playerSprite[2],characterY*blocksize,characterX*blocksize-25); break;
                    case 270: gc.drawImage(playerSprite[1],characterY*blocksize,characterX*blocksize-25); break;
                }

            }
        }.start();
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("start()");
        mainstage = primaryStage;
        //Load Data
        for (int i=0; i<indexList.length;i++) {
            mazeList[i] = indexList[i].toCharArray();
        }

        root3d.getChildren().add(world);
        root3d.setDepthTest(DepthTest.ENABLE);

        // buildScene();
        buildCamera();
        //buildAxes();
        buildMaze();

        AnchorPane globalRoot = new AnchorPane();
        moveCounter = new Label("Moves: " + moveCount);
        moveCounter.setStyle("-fx-font: 25px Tahoma;");
        moveCounter.setTextFill(Color.GRAY);
        scene3d = new Scene(globalRoot, 1024, 768, true);
        SubScene sub = new SubScene(root3d, 1024, 768, true, SceneAntialiasing.BALANCED);
        sub.setCamera(camera);
        globalRoot.getChildren().add(sub);
        globalRoot.getChildren().add(moveCounter);

        scene3d.setFill(Color.GREY);
        handleKeyboard(scene3d, world);

        scene2d = new Scene(root2d);
        handleKeyboard(scene2d,world);
        buildMaze2d();


        //handleMouse(scene, world);

        //make things to put on panes
        btnscene2d=new Button("Maze 2D");
        btnscene3d=new Button("Maze 3D");
        btnscene2d.setOnAction(e-> ButtonClicked(e));
        btnscene3d.setOnAction(e-> ButtonClicked(e));
        lblscene2d=new Label("Maze Select");
        panel2d=new FlowPane();
        panel2d.setVgap(10);
        panel2d.setStyle("-fx-background-color: tan;-fx-padding: 10px;");
        panel2d.getChildren().addAll(lblscene2d, btnscene2d,btnscene3d);
        //make 2 scenes from 2 panes
        sceneMain = new Scene(panel2d, 128, 128);

        primaryStage.setTitle("Maze Application");
        primaryStage.setScene(sceneMain);
        primaryStage.show();
    }

    public void ButtonClicked(ActionEvent e)
    {
        if (e.getSource()==btnscene3d)
            mainstage.setScene(scene3d);
        if (e.getSource()==btnscene2d)
            mainstage.setScene(scene2d);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String readFile(String fileName){
        StringBuilder sb = new StringBuilder();
        String fileText = "";

        //Read textFile
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            fileText = sb.toString();
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return fileText;
    }
}
