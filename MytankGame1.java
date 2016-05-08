package tank;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.*;

public class MytankGame1 extends JFrame{
	
	MyPanel mp=null;
	
	public static void main(String Args[]){
		MytankGame1 mtg=new MytankGame1();
		
	}
	//构造函数
	public MytankGame1()
	{
		mp=new MyPanel();
		
		this.add(mp);
		//启动mp线程
		Thread t=new Thread(mp);
		t.start();
		
		//注册监听
		
		this.addKeyListener(mp);
		
		this.setSize(400, 300);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	

}

//我的面板
/*
 * 一个类要实现监听的步骤
 * a.实现相应的接口
 * b.把接口的实现方法根据需要重新编写
 * c.在事件源注册监听
 * d.事件传递是靠事件对象
 */
class MyPanel extends JPanel implements KeyListener,Runnable
{
	int a= 200;
	int b= 200;
	int enSize=3;
	
	
	//定义一个我的坦克
	Hero hero=null;
	
	//定义敌人的坦克
	Vector<EnemyTank> ets=new Vector<EnemyTank>();
	//定义一个炸弹向量
	Vector<Bomb> bomb = new Vector<Bomb>();
	
	//定义三张图片,三张图片组成一颗炸弹
	Image image1=null;
	Image image2=null;
	Image image3=null;
	
	
	
	//构造函数
	public MyPanel()
	{
		
		
		//我的坦克的起始位置
		hero= new Hero(a,b);
		//敌人坦克的起始位置
		for(int i=0;i<enSize;i++)
		{
			//创建一辆敌人的坦克对象
			EnemyTank et = new EnemyTank((i+1)*50,0);
			
			et.setColor(1);
			et.setDirection(3);
			//启动敌人的坦克
			Thread t = new Thread(et);
			t.start();
			//给敌人坦克添加一颗子弹
			Shot s= new Shot(et.x+10,et.y+30,3);
			//子弹加入给敌人
			et.ss.add(s); 
			Thread t2 = new Thread(s);
			t2.start();
			//加入
			ets.add(et);
			
		}
		
		try{
		image3=ImageIO.read(new File("src/bomb3.png"));
		image2=ImageIO.read(new File("src/bomb2.png"));
		image1=ImageIO.read(new File("src/bomb1.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		//初始化图片
//		image3 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb3.png")); 
//		image2 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb2.png")); 
//		image1 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb1.png")); 
//		
		
		
	}
	
	//重写paint函数
	public void paint(Graphics g)
	{
		super.paint(g);
		g.fillRect(0, 0, 400, 300);
		
		//画出自己的坦克
		if(hero.isLive)
		{
			this.DrawTank(hero.getX(), hero.getY(), g, hero.direction, 0);
		}
		
		
		//画出子弹:画出多个子弹,从ss中取出每一颗子弹
		
		for(int i=0;i<this.hero.ss.size();i++)
		{
			//画出一颗子弹
			
			Shot myShot =hero.ss.get(i);
		
			
			if(myShot!=null && myShot.islive==true)
			{
				g.draw3DRect(myShot.x, myShot.y, 2, 2, false);
			}
			if(myShot.islive == false)
			{
				//从ss中删除掉该子弹
				hero.ss.remove(myShot);
			}
		
		
		
		}
		
		
		
		//画出炸弹
		for(int i=0;i<bomb.size();i++)
		{
			System.out.println("bomb.size()= "+bomb.size());
			
			//取出炸弹
			Bomb b =bomb.get(i);
			if(b.life>6)
			{
				g.drawImage(image3, b.x, b.y, 30, 30,this);
			}else if(b.life>3){
				g.drawImage(image2, b.x, b.y, 30, 30,this);
			}else{
				g.drawImage(image1, b.x, b.y, 30, 30,this);
			}
			//让b生命值减小
			b.lifeDown();
			//如果炸弹生命值为0，就把该炸弹从向量中去掉
			if(b.life==0)
			{
				bomb.remove(b);
			}
			
		}
		
		//画出敌人的坦克
		for(int i=0;i<ets.size();i++)
		{
			EnemyTank et = ets.get(i);
			if(et.isLive)
			{
				this.DrawTank  (et.getX(),et.getY(),g,et.getDirection(),et.getColor());
				//再画出敌人的子弹
				for(int j=0;j<et.ss.size();j++)
				{
					//取出子弹
					Shot enemyShot = et.ss.get(j);
					if(enemyShot.islive)
					{
						g.draw3DRect(enemyShot.x, enemyShot.y, 2, 2, false);
					}else{
						et.ss.remove(enemyShot);
					}//如果敌人的坦克死亡了，就从vector里面去掉
				}
			
			}
		}
		
	}
	
	//是否击中敌人坦克
	public void hitEnemyTank()
	{
		//判断是否击中
		for(int i=0;i<hero.ss.size();i++)
		{
			//取出子弹
			Shot myShot = hero.ss.get(i);
			//判断子弹是否活着
			if(myShot!=null&&myShot.islive==true)
			{
				//取出每一个坦克与他匹配
				for(int j=0;j<ets.size();j++)
				{
					//取出坦克
					EnemyTank et=ets.get(j);
					
					if(et.isLive)
					{
						this.hitTank(myShot, et);     
					}
				}
			}
		}
		
	}
	
	//是否击中hero坦克
	public void hitHeroTank()
	{
		//取出每一个敌人的坦克
		for(int i=0;i<this.ets.size();i++)
		{
			EnemyTank et = ets.get(i);
			//再取出每一个坦克的每一颗子弹
			for(int j=0;j<et.ss.size();j++)
			{
				//取出子弹
				Shot enemyShot = et.ss.get(j);
				
				
					this.hitTank(enemyShot, hero);
				
			}
		}
		
	}
	
	
	//写一个函数专门判断子弹是否击中目标
	public void hitTank( Shot s ,Tank et )
	{
		//判断该坦克的方向
		switch(et.direction)
		{
		//如果敌人坦克方向是上或者下
		case 0:
		case 3:
				if(s.x>et.x&&s.x<et.x+20&&s.y>et.y&&s.y<et.y+30)
				{
					//击中
					
					//子弹死亡
					s.islive=false;
					//敌人坦克死亡
					et.isLive=false;
					//创建一个炸弹放入et的vector中
					Bomb b =new Bomb(et.x,et.y);
					//放入vector
					bomb.add(b);
				}
		case 1:
		case 2:
				if(s.x>et.x&&s.x<et.x+30&&s.y>et.y&&s.y<et.y+20)
				{
					//击中
					
					//子弹死亡
					s.islive=false;
					//敌人坦克死亡
					et.isLive=false;
					Bomb b =new Bomb(et.x,et.y);
					//放入vector
					bomb.add(b);
				}
		}
	
		
	}
	
	//画出坦克的函数
	public void DrawTank  (int x,int y,Graphics g,int direct,int type) 
	{
		
		
		switch(type)
		{
		case 0:
			g.setColor(Color.gray);
			break;
		case 1:
			g.setColor(Color.yellow);
			break;
		}
		
		//判断方向
		switch(direct)
		{
		case 0://north
			//画出我的坦克(到时再封装成一个函数)
			//画出左面的矩形
			g.fill3DRect(x, y, 5, 30,true);
			g.fill3DRect(x+5, y+5, 10,	20,false);
			g.fill3DRect(x+15, y, 5, 30,true);
			g.fillOval(x+5, y+10, 10, 10);
			g.drawLine(x+10, y+15, x+10, y);
			break;
		case 1://west
			g.fill3DRect(x, y, 30, 5,true);
			g.fill3DRect(x+5, y+5, 20,	10,false);
			g.fill3DRect(x, y+15, 30, 5,true);
			g.fillOval(x+10, y+5, 10, 10);
			g.drawLine(x+15, y+10, x, y+10);
			break;
		case 2://east
			g.fill3DRect(x, y, 30, 5,true);
			g.fill3DRect(x+5, y+5, 20,	10,false);
			g.fill3DRect(x, y+15, 30, 5,true);
			g.fillOval(x+10, y+5, 10, 10);
			g.drawLine(x+15, y+10, x+30, y+10);
			break;
		case 3://south
			g.fill3DRect(x, y, 5, 30,true);
			g.fill3DRect(x+5, y+5, 10,	20,false);
			g.fill3DRect(x+15, y, 5, 30,true);
			g.fillOval(x+5, y+10, 10, 10);
			g.drawLine(x+10, y+15, x+10, y+30);
			break;
			
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() ==  KeyEvent.VK_S)
		{
			hero.direction=3;
			hero.moveDown();;
			
		}
		else if(e.getKeyCode() ==  KeyEvent.VK_W)
		{
			hero.direction=0;
			hero.moveUp();
			
		}
		else if(e.getKeyCode() ==  KeyEvent.VK_A)
		{
			hero.direction=1;
			hero.moveLeft();
			
		}
		else if(e.getKeyCode() ==  KeyEvent.VK_D)
		{
			hero.direction=2;
			hero.moveRight();
			
		}
		//判断玩家是否按下j
		if(e.getKeyCode() == KeyEvent.VK_J)
		{
			if(this.hero.ss.size()<5)
			{
				this.hero.shotEnemy();
			}
			
		}
		
	
		//调用repaint函数，来重新绘制界面
		this.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() ==  KeyEvent.VK_S)
		{
			hero.direction=3;
			hero.setY(hero.getY()+9);
			
		}
		this.repaint();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//每隔100ms去重画子弹
		while(true)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			
			this.hitEnemyTank();
			this.hitHeroTank();
			

		//重绘	
		this.repaint();
		}
			
	}
}

//炸弹类,产生后，坐标并不会改变，所以不需要做成线程
class Bomb
{
	//定义炸弹的坐标
	int x,y;
	//炸弹的生命
	int life=9;
	boolean isLive = true;
	
	public Bomb(int x,int y)
	{
		this.x=x;
		this.y=y;
		
	}
	//减少生命值
	public void lifeDown()
	{
		if(life>0)
		{
			life--;
			
		}else{
			
			this.isLive=false;
			
		}
	}
	
	
	
	

}



//子弹类
class Shot implements Runnable
{
	  int x;
	  int y;
	  int direction;
	  int speed=2;
	  //是否活着
	  boolean islive=true;
	  
	  public Shot(int x,int y,int direction)
	  {
		  
		  this.x=x;
		  this.y=y;
		  this.direction=direction;
		  
	  }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{	
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			switch(direction)
			{case 0:
				//up
				y-=speed;
				break;
			 case 1:
				 //west
				 x-=speed;
				 break;
			 case 2:
				 //east
				 x+=speed;
				 break;
			 case 3:
				 //down
				 y+=speed;
				 break;
			}
			
			//System.out.println("子弹的坐标x＝"+x+"y="+y);
			//子弹何时死亡
			
			//判断该子弹是否碰到边缘
			if(x<1 | x>399 | y<1 | y>299)
			{
				this.islive=false;
				break;
			}
			
		}
	}
}

//坦克父类
class Tank
{
	//
	boolean isLive = true;
	// 坦克的横坐标
	int x=0;
	//坦克的纵坐标
	int y=0;
	
	//坦克方向
	int direction=0;
	int color; 
	//坦克的速度
	int speed=1;
	
	
	public int getDirection()
	{
		return direction;
	}
	
	public void setDirection(int direction)
	{
		this.direction=direction;
	}
	  
	public int getColor()
	{
		return color;
	}
	
	public void setColor(int color)
	{
		this.color=color;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	 //构造函数
	public Tank(int x,int y)
	{
		this.x=x;
		this.y=y;
	}
}

//我的坦克
class Hero extends Tank 
{
	//boolean isLive = true;
	//子弹
	
	Vector<Shot> ss= new Vector<Shot>();
	Shot s=null;
	
	
	public Hero(int x,int y)
	{
		super(x,y);
		
	}
	
	//开火
	public void shotEnemy()
	{
		
		
		
		switch(this.direction)
		{
		case 0:
				//创建一颗子弹
				//Shot s1=new Shot(x+10,y,0);
			 	s=new Shot(x+10,y,0);
				//把子弹加入向量
				ss.add(s);
				break;
		case 1:
				s=new Shot(x,y+10,1);
				ss.add(s);
				break;
		case 2:
				s=new Shot(x+30,y+10,2);
				ss.add(s);
				break;
		case 3:
			s=new Shot(x+10,y+30,3);
			ss.add(s);
			break;
		}
		Thread t= new Thread(s);
		t.start();
		
	}
	
	//坦克向上运动
	public void moveUp()
	{
		
		y-=speed*5;
	}
	//坦克向右运动
	public void moveRight()
	{
		
		x+=speed*5;
	}
	public void moveDown()
	{
		
		y+=speed*5;
	}
	public void moveLeft()
	{
		
		x-=speed*5;
	}
	
}

//敌人的坦克,做成线程
class EnemyTank extends Tank implements Runnable
{
	
	//boolean isLive = true;
	int times=0;
	
	//定义一个向量存放敌人的子弹
	Vector<Shot> ss = new Vector<Shot>();
	//敌人添加子弹应该在刚刚创建坦克和敌人的坦克子弹死亡之后
	
	
	Shot s= null;
	
	
	
	public EnemyTank(int x,int y)
	{
		super(x,y);
		
		
	}

	@Override
	public void run() {
		
		while (true)
		{
			
			
		
			switch(this.direction)
			{
			case 0:
				//说明坦克正在向上移动
				//north
				for(int i=0;i<30;i++)
				{
					if( y>0)
					{
						y-=speed;
					}	
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 2:
				//east
				for(int i=0;i<30;i++)
				{
					if(x<400)
					{
						x+=speed;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					
				}
				break;
			case 3:
				//south
				for(int i=0;i<30;i++)
				{
					if(y<300)
					{
						y+=speed;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				break;
			case 1:
				//west
				for(int i=0;i<30;i++)
				{
					if(x>0 )
					{
						x-=speed;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				break;
				
			}
			
			
			this.times++;
			
			
			//判断是否需要给坦克加入新的子弹
			if(times % 2 == 0)
			{
				if(isLive)
				{
					if(ss.size()<5)
					{
						Shot s = null;
						//没有子弹了
						//添加子弹
						switch(direction)
						{
						case 0:
								//创建一颗子弹
								//Shot s1=new Shot(x+10,y,0);
							 	s=new Shot(x+10,y,0);
								//把子弹加入向量
								ss.add(s);
								break;
						case 1:
								s=new Shot(x,y+10,1);
								ss.add(s);
								break;
						case 2:
								s=new Shot(x+30,y+10,2);
								ss.add(s);
								break;
						case 3:
							s=new Shot(x+10,y+30,3);
							ss.add(s);
							break;
						}
						//启动子弹线程
						Thread t= new Thread(s);
						t.start();
					}
				}
			
			}
			
			
			//让坦克随即产生一个新的方向
			this.direction = (int) ( Math.random()*4);
			//判断敌人坦克是否死亡
			if(this.isLive == false)
			{
				//让坦克死亡后退出线程
				
				break;
			}
			
			
			
		}
		
	}
}















