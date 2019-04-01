/**
 * 该java文件用于存放成员类
 */
package hitflybirdgame;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author F Vadim
 */
//把鸟做成线程
class Bird implements Runnable {
    boolean tomedal=false;
    boolean gunkill = false;
    //鸟的总数
    //int number=Changer.getAllBirdsNum();
    //横坐标
    int x = 0;
    //纵坐标
    int y = 0;
    //鸟的方向 0右 1左
    int direct = 0;
    //鸟的速度
    int speed = Changer.getSpeed();
    int color;
    boolean isNeed = true;
    boolean isLive = true;
    int life = 999999;

    public Bird(int x, int y, int direct, int color) {
        this.x = x;
        this.y = y;
        this.direct = direct;
        this.color = color;
    }

    //减少生命
    public void lifedown() {
        if (life > 0) {
            life--;
        } else {
            this.isNeed = false;
            this.isLive = false;

        }
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

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    int num = 1;

    @Override
    public void run() {
        while (true) {
            if (this.isLive == true) {
                switch (this.direct) {
                    case 0:
                        //说明鸟向右飞
                        for (int i = 0; i < 1; i++) {
                            if (x < 800) {
                                x += speed;
                            } else {
                                this.isNeed = false;
                                this.isLive = false;
                            }
                            //System.out.println("x为"+x);
                            try {
                                Thread.sleep(50);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        if (this.isLive == false && gunkill == false) {
                            Recorder.reduceBirdNum();
                            // System.out.println("死于右边");
                        }
                        break;
                    case 1:
                        //鸟向左飞
                        for (int i = 0; i < 1; i++) {
                            if (x > -48) {
                                x -= speed;
                            } else {
                                this.isNeed = false;
                                this.isLive = false;

                            }
                            //System.out.println("x为"+x);
                            try {
                                Thread.sleep(50);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        if (this.isLive == false && gunkill == false) {
                            Recorder.reduceBirdNum();
                            //System.out.println("死于左边");
                        }
                        break;
                }
            } else if (this.isLive == false) {
                //让鸟死亡后下落后退出进程
                switch (this.direct) {
                    case 0:
                        //说明鸟向右飞
                        for (int i = 0; i < 30; i++) {
                            //此处违反物理，仅为使抛物运动更明显，向左飞亦同
                            x += speed + 1;
                            y += num;
                            num += 2;
                            //System.out.println("x为"+x);
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        break;
                    case 1:
                        //鸟向左飞
                        for (int i = 0; i < 30; i++) {
                            x -= speed + 1;
                            y += num;
                            num += 2;

                            //System.out.println("x为"+x);
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        break;
                }
                //退出
                break;
            }

        }
        this.isNeed = false;
    }
}
//枪

class Gun implements Runnable{

    //子弹
    //Shot s=new Shot();
    static int x;
    static int y;
    MyPanel mptemp;
    public Gun(int x, int y,MyPanel mp) {
        this.x = x;
        this.y = y;
        mptemp=mp;
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

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       while(true){
        while(mptemp.kill&&Recorder.getBullet()>0)
            {
              
            Recorder.reduceBullet();
            mptemp.killBird(mptemp.gun.x, mptemp.gun.y, mptemp.birds);
            try {
                Thread.sleep(110);
            } catch (Exception e) {
            }
            try {
                AePlayWave as = new AePlayWave("src/music/shot.wav");
                as.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            
             mptemp.repaint(); 
             
                
            }
    }
    }

}
//子弹类
class Shot implements Runnable
{
    int x;
    int y;
//    int direct;
    int speed=25;
    double xspeed=Gun2.tempx(speed);
    double yspeed=Gun2.tempy(speed);
    //是否还活着
    boolean isLive=true;
    public Shot(int x,int y)
    {
        this.x=x;
        this.y=y;
    }

    @Override
    public void run() {
        while(true)
        {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }

            x-=xspeed;
            y-=yspeed;


            //System.out.println("子弹现在的位置"+"x="+x+","+"y="+y);
            //子弹何时死亡？？？？？？
            
            //判断该子弹是否碰到边缘
            if(x<0||x>800||y<0||y>600)
            {
                this.isLive=false;
                break;
            }
        }
    }
   
}
//纪录类，同时可以保存玩家的记录
//该类是代码的核心部分，以后修改时需慎重考虑

class Recorder{
    static int mode=0;
    //记录每关有多少敌人
    private static int bdsNum = 50;
    private static int allbullet = 90;
    private static int bullet = 10;
    private static int score = 0;
    //从文件中恢复记录点
    private static ArrayList<Node> nodes = new ArrayList<Node>();
    //加分
    private static int reward = 5;
    private static int extraReward = 10;
    private static int bulletReward = 1;
    private static int killNum = 0;
    private static FileReader fr = null;
    private static BufferedReader br = null;
    private static FileWriter fw = null;
    private static BufferedWriter bw = null;
    private static ArrayList<Bird> bds = new ArrayList<Bird>();
    private static int bestScore = 0;
    //测试
    static MyListener lis = new MyListener() {
        @Override
        public void eventNeedClose(MyEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };

    public static void addMyListener(MyListener listener) {
        lis = listener;
    }

    //完成读取鸟信息任务
    public static ArrayList<Node> getScoreAndNodes() {
        try {
            fr = new FileReader("D:\\myRecorder.ini");
            br = new BufferedReader(fr);
            String n = "";
            //先读取五行
            n = br.readLine();
            bestScore = Integer.parseInt(n);
            n = br.readLine();
            myhitflybirdgame.playMusic=Boolean.parseBoolean(n);
            n = br.readLine();
            bdsNum = Integer.parseInt(n);
            n = br.readLine();
            bullet = Integer.parseInt(n);
            n = br.readLine();
            allbullet = Integer.parseInt(n);
            n = br.readLine();
            score = Integer.parseInt(n);
            n = br.readLine();
            killNum = Integer.parseInt(n);
            n = br.readLine();
            Changer.setSpeed(Integer.parseInt(n));
            Init.setSpeed(Integer.parseInt(n));
            n = br.readLine();
            Changer.setSrcNum(Integer.parseInt(n));
            Init.setSrcNum(Integer.parseInt(n));
            while ((n = br.readLine()) != null) {
                String[] wxyz = n.split(" ");
                Node node = new Node(Integer.parseInt(wxyz[0]), Integer.parseInt(wxyz[1]), Integer.parseInt(wxyz[2]), Integer.parseInt(wxyz[3]));
                nodes.add(node);
            }

        } catch (Exception e) {
            //e.printStackTrace();
            JOptionPane.showMessageDialog(null, "未发现存档，按确认键开始新游戏", "警告", JOptionPane.WARNING_MESSAGE);

        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nodes;
    }

    //从文件中读取成绩和普通资料
//    public static void getNumRecord()
//    {
//        try {
//            fr=new FileReader("D:\\天天向上\\netbeans\\myRecorder.ini");
//            br=new BufferedReader(fr);
//            String n=br.readLine();
//            bdsNum=Integer.parseInt(n);
//            n=br.readLine();
//            bullet=Integer.parseInt(n);
//            n=br.readLine();
//            allbullet=Integer.parseInt(n);
//            n=br.readLine();
//            score=Integer.parseInt(n);
//            n=br.readLine();
//            killNum=Integer.parseInt(n);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            try {
//                br.close();
//                fr.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }     
    //保存游戏信息到文件
    public static void keepRecAndBirds() {
        try {
            //创建
            fw = new FileWriter("D:\\myRecorder.ini");
            bw = new BufferedWriter(fw);

            bw.write(bestScore + "\r\n");
            bw.write(myhitflybirdgame.playMusic+ "\r\n");
            bw.write(bdsNum + "\r\n");
            bw.write(bullet + "\r\n");
            bw.write(allbullet + "\r\n");
            bw.write(score + "\r\n");
            bw.write(killNum + "\r\n");
            bw.write(Changer.getSpeed() + "\r\n");
            bw.write(Changer.getSrcNum() + "\r\n");
            //保存当前还活着的鸟的坐标方向类型
            for (int i = 0; i < bds.size(); i++) {
                Bird bd = bds.get(i);
                if (bd.isLive) {
                    //活着就保存
                    String record = bd.x + " " + bd.y + " " + bd.direct + " " + bd.color;
                    //写入
                    bw.write(record + "\r\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //后开先关闭
                bw.close();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static int getBestScore() {
        return bestScore;
    }

    //把玩家成绩保存到文件中
//    public static void keepRecord()
//    {
//        try {
//            //创建
//            fw=new FileWriter("D:\\天天向上\\netbeans\\myRecorder.ini");
//            bw=new BufferedWriter(fw);
//            
//            bw.write(bdsNum+"\r\n");
//            bw.write(bullet+"\r\n");
//            bw.write(allbullet+"\r\n");
//            bw.write(score+"\r\n");
//            bw.write(killNum+"\r\n");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            try {
//                //后开先关闭
//                bw.close();
//                fw.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    public static void setBestScore(int bestScore) {
        Recorder.bestScore = bestScore;
    }

    public static int getBdsNum() {
        return bdsNum;
    }

    public static void setBdsNum(int bdsNum) {
        Recorder.bdsNum = bdsNum;
    }

    public static int getAllbullet() {
        return allbullet;
    }

    public static void setAllbullet(int allbullet) {
        Recorder.allbullet = allbullet;
    }

    public static int getBullet() {
        return bullet;
    }

    public static void setBullet(int bullet) {
        Recorder.bullet = bullet;
    }
    //防止监听触发多次
    static int temp = 0;

    //减少鸟数
    public static void reduceBirdNum() {
        bdsNum--;
        //发送消息
        if (bdsNum == 0 && temp == 0) {
            System.out.println("进入减少后鸟死完了");
            MyEvent e = new MyEvent();
//        if(e.times==0)
//        {
            lis.eventNeedClose(e);
            //e.times++;
            //}
            temp++;
        }
    }

    public static void reduceAllBullet() {
        allbullet--;

    }

    public static void reduceBullet() {
        bullet--;
        if (bullet <= 0 && allbullet <= 0 && temp == 0) {
            System.out.println("进入减少后子弹为0");
            MyEvent e = new MyEvent();
//        if(e.times==0)
//        {
            lis.eventNeedClose(e);
            //e.times++;
            // }
            temp++;
        }
    }

    public static void addBdsNum() {
        bdsNum++;
    }

    public static void addkillNum() {
        killNum++;
    }

    public static void addscore() {
        score += reward;
    }

    public static void addextrascore() {
        score += extraReward;
    }

    public static void addBullet() {
        allbullet += bulletReward;
    }

    public static int getScore() {
        return score;
    }

    public static void setScore(int score) {
        Recorder.score = score;
    }

    public static int getReward() {
        return reward;
    }

    public static void setReward(int reward) {
        Recorder.reward = reward;
    }

    public static int getExtraReward() {
        return extraReward;
    }

    public static void setExtraReward(int extraReward) {
        Recorder.extraReward = extraReward;
    }

    public static int getBulletReward() {
        return bulletReward;
    }

    public static void setBulletReward(int bulletReward) {
        Recorder.bulletReward = bulletReward;
    }

    public static int getKillNum() {
        return killNum;
    }

    public static void setKillNum(int killNum) {
        Recorder.killNum = killNum;
    }

    public static ArrayList<Bird> getBds() {
        return bds;
    }

    public static void setBds(ArrayList<Bird> bds) {
        Recorder.bds = bds;
    }



}
//用于io流存放和读取鸟的坐标和其他属性

class Node {

    int x;
    int y;
    int direct;
    int color;

    public Node(int x, int y, int direct, int color) {
        this.x = x;
        this.y = y;
        this.direct = direct;
        this.color = color;
    }

}
//这是我的接口，里面的函数是在事件（鸟死完或者子弹打完）产生后进入结算页面

interface MyListener {

    public void eventNeedClose(MyEvent e);
}
//这是我的事件，不需要实现任何东西

class MyEvent {
    //此处为计数器
    //static int times = 0;
//private int BirdNum;
//private int Bullet;
//private int allBullet;
//
//    public MyEvent(int BirdNum, int Bullet, int allBullet) {
//        this.BirdNum = BirdNum;
//        this.Bullet = Bullet;
//        this.allBullet = allBullet;
//    }
//
//    public int getBirdNum() {
//        return BirdNum;
//    }
//
//    public void setBirdNum(int BirdNum) {
//        this.BirdNum = BirdNum;
//    }
//
//    public int getBullet() {
//        return Bullet;
//    }
//
//    public void setBullet(int Bullet) {
//        this.Bullet = Bullet;
//    }
//
//    public int getAllBullet() {
//        return allBullet;
//    }
//
//    public void setAllBullet(int allBullet) {
//        this.allBullet = allBullet;
//    }

}
//该类用于存放鸟的速度和并发，可以考虑和recorder合并

class Changer {

    private static int speed = 5;
    private static int srcNum = 6;
    // private static int allBirdsNum=50;

//    public static int getAllBirdsNum() {
//        return allBirdsNum;
//    }
//
//    public static void setAllBirdsNum(int allBirdsNum) {
//        Changer.allBirdsNum = allBirdsNum;
//    }
//
    public static int getSpeed() {
        return speed;
    }

    public static void setSpeed(int speed) {
        Changer.speed = speed;
    }

    public static int getSrcNum() {
        return srcNum;
    }

    public static void setSrcNum(int srcNum) {
        Changer.srcNum = srcNum;
    }

}
//这是设置游戏难度和模式的窗口

class MyFrame extends JFrame implements ItemListener, ActionListener {

    JPanel jp1 = null;
    JPanel jp2 = null;
    JPanel jp3 = null;
    JPanel jp4 = null;
    JPanel jp5 = null;
    JPanel jp6 = null;
    JPanel jp7 = null;
    ButtonGroup bg = null;
    JRadioButton jrb1 = null;
    JRadioButton jrb2 = null;
    JRadioButton jrb3 = null;
    JRadioButton jrb4 = null;
    JComboBox jcb1 = null;
    JComboBox jcb2 = null;

    public MyFrame() {

        Font f1 = new Font("宋体", Font.BOLD, 20);
        //定义panel
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();
        jp4 = new JPanel();
        jp5 = new JPanel();
        jp6 = new JPanel();
        jp7 = new JPanel();
        bg = new ButtonGroup();
        jrb1 = new JRadioButton("正常难度", false);
        jrb2 = new JRadioButton("挑战模式", false);
        jrb3 = new JRadioButton("无尽模式", false);
        jrb4 = new JRadioButton("自选模式", false);
        String[] speed = {"4", "5", "6", "7", "8", "9", "10"};
        jcb1 = new JComboBox(speed);
        String[] srcnum = {"6", "8", "10", "12", "14", "16"};
        jcb2 = new JComboBox(srcnum);
        JLabel jl1 = new JLabel("速度:");
        jcb1.setEnabled(false);
        JLabel jl2 = new JLabel("并发:");
        jcb2.setEnabled(false);
        JButton jb1 = new JButton("确定");
        JButton jb2 = new JButton("取消");
        bg.add(jrb1);
        bg.add(jrb2);
        bg.add(jrb3);
        bg.add(jrb4);
        //设置布局
        this.setLayout(new GridLayout(7, 1));
        jp1.add(jrb1);
        jp2.add(jrb2);
        jp3.add(jrb3);
        jp4.add(jrb4);
        jp5.add(jl1);
        jp5.add(jcb1);
        jp6.add(jl2);
        jp6.add(jcb2);
        jp7.add(jb1);
        jp7.add(jb2);
        this.add(jp1);
        this.add(jp2);
        this.add(jp3);
        this.add(jp4);
        this.add(jp5);
        this.add(jp6);
        this.add(jp7);
        //注册监听
        jrb1.addItemListener(this);
        jrb2.addItemListener(this);
        jrb3.addItemListener(this);
        jrb4.addItemListener(this);
        jb1.addActionListener(this);
        jb2.addActionListener(this);
        jb1.setActionCommand("sure");
        jb2.setActionCommand("cancel");
        this.setSize(200, 400);
        this.setResizable(true);
        this.setLocation(1200, 200);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (e.getStateChange() == ItemEvent.SELECTED) {

            if (e.getItem() == jrb1) {
                jcb1.setEnabled(false);
                jcb2.setEnabled(false);
                System.out.println("按下jrb1");
                a = 1;
            } else if (e.getItem() == jrb2) {
                jcb1.setEnabled(false);
                jcb2.setEnabled(false);
                System.out.println("按下jrb2");
                a = 2;
            } else if (e.getItem() == jrb3) {
                jcb1.setEnabled(false);
                jcb2.setEnabled(false);
                System.out.println("按下jrb3");
                a = 3;
            } else if (e.getItem() == jrb4) {
                System.out.println("按下jrb4");
                jcb1.setEnabled(true);
                jcb2.setEnabled(true);
                a = 4;
            }
//       else if(e.getItemSelectable()==jb1)
//       {
//            Changer.setSpeed(jcb1.getSelectedIndex());
//          Changer.setSrcNum(jcb1.getSelectedIndex());
//           Recorder.setBdsNum(50);
//           System.out.println("hitflybirdgame.MyFrame.itemStateChanged()");
//       }

        }
    }
    int a = 5;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (a == 1 && e.getActionCommand().equals("sure")) {
            //设置
            Recorder.setBdsNum(50);
            Changer.setSpeed(5);
            Changer.setSrcNum(6);
            Init.setBdsNum(50);
            Init.setSpeed(5);
            Init.setSrcNum(6);
            jcb1.setEnabled(false);
            jcb2.setEnabled(false);
            //初始化分数击杀弹夹
            Recorder.setScore(0);
            Recorder.setKillNum(0);
            Recorder.setBullet(10);
            Recorder.setAllbullet(90);
            try {
                AePlayWave as = new AePlayWave("src/music/提示.wav");
                as.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (a == 2 && e.getActionCommand().equals("sure")) {
            Recorder.setBdsNum(80);
            Changer.setSpeed(9);
            Changer.setSrcNum(10);
            Init.setBdsNum(80);
            Init.setSpeed(9);
            Init.setSrcNum(10);
            //初始化分数击杀弹夹
            Recorder.setScore(0);
            Recorder.setKillNum(0);
            Recorder.setBullet(10);
            Recorder.setAllbullet(90);
            try {
                AePlayWave as = new AePlayWave("src/music/提示.wav");
                as.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (a == 3 && e.getActionCommand().equals("sure")) {
            Changer.setSpeed(5);
            Changer.setSrcNum(8);
            Recorder.setBdsNum(999);
            Init.setSpeed(5);
            Init.setSrcNum(8);
            Init.setBdsNum(999);
            //初始化分数击杀弹夹
            Recorder.setScore(0);
            Recorder.setKillNum(0);
            Recorder.setBullet(10);
            Recorder.setAllbullet(90);
            try {
                AePlayWave as = new AePlayWave("src/music/提示.wav");
                as.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (a == 4 && e.getActionCommand().equals("sure")) {
            Changer.setSpeed(Integer.parseInt(jcb1.getSelectedItem().toString()));
            Changer.setSrcNum(Integer.parseInt(jcb2.getSelectedItem().toString()));
            Recorder.setBdsNum(50);
            Init.setSpeed(Integer.parseInt(jcb1.getSelectedItem().toString()));
            Init.setSrcNum(Integer.parseInt(jcb2.getSelectedItem().toString()));
            Init.setBdsNum(50);
            //初始化分数击杀弹夹
            Recorder.setScore(0);
            Recorder.setKillNum(0);
            Recorder.setBullet(10);
            Recorder.setAllbullet(90);
            System.out.println("speed" + Integer.parseInt(jcb1.getSelectedItem().toString()));
            System.out.println("srcnum" + Integer.parseInt(jcb2.getSelectedItem().toString()));
            try {
                AePlayWave as = new AePlayWave("src/music/提示.wav");
                as.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        this.dispose();
        //this.setVisible(false);
    }
}
//这个类用于给restart后的变量赋值

class Init {

    //三个初值
    private static int bdsNum = 50;
    private static int speed = 5;
    private static int srcNum = 6;

    public static int getBdsNum() {
        return bdsNum;
    }

    public static void setBdsNum(int bdsNum) {
        Init.bdsNum = bdsNum;
    }

    public static int getSpeed() {
        return speed;
    }

    public static void setSpeed(int speed) {
        Init.speed = speed;
    }

    public static int getSrcNum() {
        return srcNum;
    }

    public static void setSrcNum(int srcNum) {
        Init.srcNum = srcNum;
    }
    

}

class Gun2 implements Runnable{
  static  double x=400, y=600,x3,y3;
  Shot s=null;
  Vector<Shot> ss=new Vector<Shot>();
  static MyPanel mptemp;
  public Gun2(MyPanel mp){
    mptemp=mp;
}
  static Image image=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/包邮.png"));
  public static void drawgun(int x1,int y1,Graphics2D g){
      g.setColor(Color.black);
      x3=(x-(35*(x-x1-15)/Math.sqrt((y-y1-15)*(y-y1-15)+(x-x1-15)*(x-x1-15))));
      y3=(y-(35*(y-y1-15))/Math.sqrt((y-y1-15)*(y-y1-15)+(x-x1-15)*(x-x1-15)));
      //g.drawLine((int)x, (int)y, (int)(x-Math.sqrt(100/(1+(y-y1)/(x-x1)))),(int)(y-Math.sqrt(100-(x-Math.sqrt(100/(1+(y-y1)/(x-x1))))*(x-Math.sqrt(100/(1+(y-y1)/(x-x1)))))));
      g.setStroke(new BasicStroke(1.6f));
      //画出枪
      //g.drawArc((int)x-30, (int)y-30, 60, 60, 0, 180);
      //g.drawOval((int)x-30, (int)y-30,60, 60);
      g.setColor(Color.BLACK);
      g.drawLine((int)x, (int)y, (int)x3, (int)y3);
      g.drawLine((int)x+5, (int)y, (int)x3+5, (int)y3);
      g.drawLine((int)x+1, (int)y, (int)x3+1, (int)y3);
      g.drawLine((int)x+2, (int)y, (int)x3+2, (int)y3);
      g.drawLine((int)x+3, (int)y, (int)x3+3, (int)y3);
      g.drawLine((int)x+4, (int)y, (int)x3+4, (int)y3);
      g.drawLine((int)x3+5, (int)y3, (int)x3, (int)y3);
      g.drawImage(image, (int)x-30, (int)y-30, 60, 69,mptemp);
  }
    public void shotEnemy()
    {
//        int direct;
//        if(x3<=x){
//            direct=0;
//        }else{
//            direct=1;
//        }
        //创建一颗子弹
        s=new Shot((int)x3, (int)y3);
        //把子弹加入向量
        ss.add(s);
        Thread t=new Thread(s);
        t.start();
    }
    public static double tempx(int speed)
    {
        double tempx;
        tempx=(speed*(x-Gun.x-15)/Math.sqrt((y-Gun.y-15)*(y-Gun.y-15)+(x-Gun.x-15)*(x-Gun.x-15)));
        return tempx;
    }
    public static double tempy(int speed)
    {
        double tempy;
        tempy=(speed*(y-Gun.y-15))/Math.sqrt((y-Gun.y-15)*(y-Gun.y-15)+(x-Gun.x-15)*(x-Gun.x-15));
        return tempy;
    }
    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       while(true){
        while(mptemp.kill&&Recorder.getBullet()>0)
            {
              
            Recorder.reduceBullet();
            shotEnemy();
            //mptemp.killBird(mptemp.gun.x, mptemp.gun.y, mptemp.birds);
            try {
                Thread.sleep(150);
            } catch (Exception e) {
            }
            try {
                AePlayWave as = new AePlayWave("src/music/shot.wav");
                as.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            
             mptemp.repaint(); 
             
                
            }
    }
    }

}