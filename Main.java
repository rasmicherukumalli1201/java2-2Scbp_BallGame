import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// draws bricks onto mentioned screen, unsets bricks, stores active brick details 
class BlocksGridGenerator {
	public int BlocksGrid [][];
	private int TOTAL_BLOCKS_WIDTH = 540;
	private int TOTAL_BLOCKS_HEIGHT = 150;
	
	public int brickWidth;
	public int brickHeight;
	
	// this creates the bricks of size 3x6
	public BlocksGridGenerator(int rows, int cols) {
		BlocksGrid = new int [rows][cols];

		// if block is never hit, its value is 1 elsewise 0
		for (int r=0; r<rows; r++) 
			for (int c=0; c< cols; c++)
				BlocksGrid[r][c] = 1;
		
		brickWidth  = TOTAL_BLOCKS_WIDTH/cols;
		brickHeight = TOTAL_BLOCKS_HEIGHT/rows;
	}
	
	// this draws the bricks
	public void draw(Graphics2D g) {
		int rows = BlocksGrid.length;
		int cols = BlocksGrid[0].length;
		for (int r = 0; r < rows; r++) {
			for (int c=0; c< cols; c++) {
				// if block is never hit, its value is 1 elsewise 0
				if(BlocksGrid[r][c] > 0) {
					// block color
					g.setColor(new Color(0XFFD6A5)); 
					// x,y, width, height(downwards)
					g.fillRect(
						c*brickWidth + 80, r*brickHeight + 50, brickWidth, brickHeight
						);
					
					g.setStroke(new BasicStroke(4));
					g.setColor(Color.BLACK);
					g.drawRect(
						c*brickWidth + 80, r*brickHeight + 50, brickWidth, brickHeight
					);
				}
			}
			
		}
	}
	
	// this sets the value of brick to 0 if it is hit by the ball
	public void setBrickValue(int value, int row, int col) 
		{ BlocksGrid[row][col] = value; }
}


class GamePlay extends JPanel implements KeyListener, ActionListener  {
	private boolean alive = true;
	private int score = 0;
	
	private int totalBricks = 18; //(3*6)
	
	private Timer timer;
	private int delay = 8;
	
	private int playerX = 310;
	
	private int ballposX = 120;
	private int ballposY = 350;
	private int ballXdir = -1;
	private int ballYdir = -2;
	
	private BlocksGridGenerator AllBricks;
	private int rows= 3;
	private int cols= 6;
	
// Gameplay constructor
	public GamePlay() {
		AllBricks = new BlocksGridGenerator(rows, cols);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		timer = new Timer(delay, this);
		timer.start();
	}

	public void paint(Graphics g) {
		
		g.setColor(new Color(0XFF9B9B)); // background color
		g.fillRect(0, 0, 692, 592);
		
		AllBricks.draw((Graphics2D)g);
		
		g.setColor(new Color(0XA76F6F));  // pad color
		g.fillRect(playerX, 550, 100, 12);
		
		g.setColor(new Color(0XFF2171));  // ball color
		g.fillOval(ballposX, ballposY, 20, 20);
		
		g.setColor(Color.black);
		g.setFont(new Font("MV Boli", Font.BOLD, 25));
		g.drawString("Score: " + score, 520, 30);
		
		
		if (totalBricks <= 0) { // if all bricks are destroyed then you win
			alive = false;
			ballXdir = 0;
			ballYdir = 0;
			g.setColor(new Color(0XFF6464));
			g.setFont(new Font("MV Boli", Font.BOLD, 30));
			g.drawString("You Won, Score: " + score, 190, 300);
			
			g.setFont(new Font("MV Boli", Font.BOLD, 20));
			g.drawString("Press Enter to Restart.", 230, 350);
		}

		// if ball goes below the paddle then you lose 
		if(ballposY > 570) { 
			alive = false;
			ballXdir = 0;
			ballYdir = 0;
			g.setColor(Color.BLACK);
			g.setFont(new Font("MV Boli", Font.BOLD, 30));
			g.drawString("Game Over, Score: " + score, 190, 300);
			
			g.setFont(new Font("MV Boli", Font.BOLD, 20));
			g.drawString("Press Enter to Restart", 230, 350);
				
		} 
		g.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		timer.start();
		if(alive) {
			// Ball - Pedal  interaction 
			if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) 	
				{ ballYdir = - ballYdir; }

			for( int r = 0; r<rows; r++) { // Ball - Brick interaction
				for(int c = 0; c<cols; c++) {  // BlocksGrid.BlocksGrid[0].length is the number of columns
					if(AllBricks.BlocksGrid[r][c] > 0) {
						int brickX = c*AllBricks.brickWidth + 80;
						int brickY = r*AllBricks.brickHeight + 50;
						int brickWidth= AllBricks.brickWidth;
						int brickHeight = AllBricks.brickHeight;
						
						Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
						Rectangle ballRect = new Rectangle(ballposX, ballposY, 20,20);
						
						if(ballRect.intersects(brickRect) ) {
							AllBricks.setBrickValue(0, r, c);
							totalBricks--;
							score+=5;
							// if edge blocks hit sideways
							if(ballposX + 19 <= brickRect.x || ballposX +1 >= brickRect.x + brickRect.width) 
								ballXdir = -ballXdir;
							// else if ball hits vertically
							 else {
								ballYdir = -ballYdir;
							}
						}
					}
				}
			}
			
			ballposX += ballXdir;
			ballposY += ballYdir;
			// if ball hits the left wall then it bounces back
			if(ballposX < 0)  ballXdir = -ballXdir;
			// if ball hits the top wall then it bounces back
			if(ballposY < 0)  ballYdir = -ballYdir; 
			// if ball hits the right wall then it bounces back
			if(ballposX > 670)ballXdir = -ballXdir;  
		}
		repaint();
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// if right arrow key is pressed then paddle moves right
		if(arg0.getKeyCode() == KeyEvent.VK_RIGHT || arg0.getKeyCode() == KeyEvent.VK_D ) { 
			if(playerX >= 600) playerX = 600;
			else moveRight(); 
		}
		// if left arrow key is pressed then paddle moves left
		if(arg0.getKeyCode() == KeyEvent.VK_LEFT || arg0.getKeyCode() == KeyEvent.VK_A ) 
		{
			if(playerX < 10) playerX = 10;
			else moveLeft(); 
		}
		
		// if enter key when not alive is pressed then game restarts
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) { 
			if(!alive) {
				alive = true;
				ballposX = 120;
				ballposY = 350;
				ballXdir = -1;
				ballYdir = -2;
				score = 0;
				totalBricks = 21;
				AllBricks = new BlocksGridGenerator(rows,cols);
				
				repaint();
			}
		}
		
	}	
		public void moveRight() { // paddle moves right by 50 pixels
			alive = true;
			playerX += 50;
		}
		public void moveLeft() { // paddle moves left by 50 pixels
			alive = true;
			playerX -= 50;
		}
		
	

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}


}
class Main {
	public static void main(String[] args) {
		JFrame obj = new JFrame();
		GamePlay gamePlay = new GamePlay();
		obj.setBounds(10, 10, 700, 600);
		obj.setTitle("Brick Breaker");
		obj.setResizable(false);
		obj.setVisible(true);
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		obj.add(gamePlay);
	} 
}