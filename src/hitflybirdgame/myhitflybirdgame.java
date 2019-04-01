/**
 * 存盘退出游戏，可以记录当时的鸟的坐标并可以恢复
 * 可以重新游戏，游戏菜单栏实现多种功能
 * 一群飞鸟随机出现并飞离窗体
 * 使用鼠标可击落，配有枪声，击中声，换弹声
 * 下落满足物理定律,下落后击落可特殊加分
 * 动态显示数据，动态评级
 * 游戏节奏变化，更具趣味性
 * 不同鸟有不同加成
 * 游戏开始暂停实现
 * 游戏开火模式控制
 * 设置鸟的属性,选择游戏难度
 * 设置游戏结束结算界面，结算条件为Recorder.getBdsNum()==0||((Recorder.getBullet()==0)&&(Recorder.getAllbullet()==0))
 * 单写一个纪录类完成对玩家的记录,结算时为玩家评级
 * 修改玩家最高分时要先缓存所有数据修改第一行，然后再写入文件
 * 要点：当玩家没有建立存档时新建只存储最高分的存档，暂时没有发现问题
 * 实验总结:游戏内容尽量用线程来实现，可以较小地消耗资源
 * 有几个重大难题尚未解决:1.音频文件流+线程的实现，导致生成exe后没有声音
 *                        2.生成exe后结算界面的restart按钮加载不了图片
 */
package hitflybirdgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
//import sun.audio.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author F Vadim
 */
public class myhitflybirdgame extends JFrame implements ActionListener, MyListener, KeyListener {

    //全局音频变量
    AePlayWave ae = null;
    //控制音乐是否播放
    static boolean playMusic=true;
    //定义战斗面板
    MyPanel mp = null;
    //定义一个开始面板
    MyStartPanel msp = null;
    //定义一个结束面板
    MyFinishPanel mfp = null;
    //做出我需要的菜单
    JMenuBar jmb = null;
    //开始游戏
    JMenu jm1 = null;
    JMenuItem jmi1 = null;
    JMenuItem jmi2 = null;
    JMenuItem jmi3 = null;
    JMenuItem jmi4 = null;
    //游戏规则
    JMenu jm2 = null;
    JMenuItem jmi5 = null;
    //游戏设置
    JMenu jm3 = null;
    JMenuItem jmi6 = null;
    JMenuItem jmi7 = null;
    JMenuItem jmi8 = null;
    JMenuItem jmi9 = null;
    //重新游戏按钮
    JButton myjb = null;
    FileReader fr = null;
    BufferedReader br = null;

    public static void main(String[] args) {
        myhitflybirdgame mygame = new myhitflybirdgame();
    }

    public myhitflybirdgame() {
        //首先初始化最高分
        try {
            fr = new FileReader("D:\\myRecorder.ini");
            br = new BufferedReader(fr);
            String n = "";
            //先读取最高分
            n = br.readLine();
            Recorder.setBestScore(Integer.parseInt(n));
            //在读取是否播放音乐
            n = br.readLine();
            playMusic=Boolean.parseBoolean(n);
        } catch (Exception e) {
            //没有建立文件就初始化最高成绩为0
            Recorder.setBestScore(0);
            //JOptionPane.showMessageDialog(this, "未发现存档", "未发现存档", JOptionPane.ERROR_MESSAGE);  
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
        //放BGM
        try {
            if(playMusic){
            ae = new AePlayWave("src/music/开始.wav");
            ae.start();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Recorder re = new Recorder();
        //创建菜单以及菜单选项
        jmb = new JMenuBar();
        jm1 = new JMenu("游戏(G)");
        //设置快捷方式
        jm1.setMnemonic('G');
        //开始游戏
        jmi1 = new JMenuItem("开始新游戏(N)");
        jmi1.setMnemonic('N');
        //对jmi1响应
        jmi1.addActionListener(this);
        jmi1.setActionCommand("newgame");
        //退出游戏
        jmi2 = new JMenuItem("退出游戏(E)");
        jmi2.setMnemonic('E');
        jmi2.addActionListener(this);
        jmi2.setActionCommand("exit");
        //存盘退出
        jmi3 = new JMenuItem("存盘退出(S)");
        jmi3.setEnabled(false);
        jmi3.setMnemonic('S');
        jmi3.addActionListener(this);
        jmi3.setActionCommand("saveExit");
        //续上局
        jmi4 = new JMenuItem("继续上局(C)");
        jmi4.setMnemonic('C');
        jmi4.addActionListener(this);
        jmi4.setActionCommand("continue");
        //游戏规则
        jm2 = new JMenu("游戏说明");
        jmi5 = new JMenuItem("游戏规则");
        jmi5.addActionListener(this);
        jmi5.setActionCommand("illustrate");
        //游戏设置（难度设置）
        jm3 = new JMenu("游戏设置");
        jmi6 = new JMenuItem("难度及模式");
        jmi6.addActionListener(this);
        jmi6.setActionCommand("set");
        jmi7 = new JMenuItem("关闭背景音乐");
        jmi7.addActionListener(this);
        jmi7.setActionCommand("close");
        jmi8 = new JMenuItem("开启1号BGM");
        jmi8.addActionListener(this);
        jmi8.setActionCommand("open1");
        jmi9 = new JMenuItem("开启2号BGM");
        jmi9.addActionListener(this);
        jmi9.setActionCommand("open2");
        //加入组件
        jm1.add(jmi1);
        jm1.add(jmi4);
        jm1.add(jmi3);
        jm1.add(jmi2);
        jm2.add(jmi5);
        jm3.add(jmi6);
        jm3.add(jmi7);
        jm3.add(jmi8);
        jm3.add(jmi9);
        jmb.add(jm1);
        jmb.add(jm2);
        jmb.add(jm3);
        msp = new MyStartPanel();
        Thread t = new Thread(msp);
        t.start();
        re.addMyListener(this);
        this.setJMenuBar(jmb);
        this.add(msp);
        this.setResizable(false);
        this.setLocation(600, 100);
        this.setSize(800, 765);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        //设置焦点，将监听焦点给this，这是大坑，以后注意参考
        this.setFocusable(true);
        //清除输入法控制
        this.enableInputMethods(false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //对用户不同的点击做出不同的处理
        if (e.getActionCommand().equals("newgame")) {
            //开启存盘退出按钮
            jmi3.setEnabled(true);
            mp = new MyPanel("newGame");
            // 启动mp线程
            Thread t = new Thread(mp);
            t.start();
            //先删除旧的面板
            this.remove(msp);
            this.add(mp);
            //显示
            this.setVisible(true);
            //注册监听
            this.addMouseMotionListener(mp);
            this.addMouseListener(mp);
            this.addKeyListener(this);
            //关闭新游戏和继续游戏按钮
            jmi1.setEnabled(false);
            jmi4.setEnabled(false);
        } else if (e.getActionCommand().equals("exit")) {
            //保存用户击毁数量
            //Recorder.keepRecord();
            //用户退出
            System.exit(0);
        } else if (e.getActionCommand().equals("saveExit")) {
            //存盘退出
            //保存成绩和鸟的坐标
            Recorder.setBds(mp.birds);
            Recorder.keepRecAndBirds();
            //用户退出 
            System.exit(0);
        } else if (e.getActionCommand().equals("continue")) {

            //恢复
            //开启存盘退出按钮
            jmi3.setEnabled(true);
            mp = new MyPanel("con");

            // 启动mp线程
            Thread t = new Thread(mp);
            t.start();
            //先删除旧的面板
            this.remove(msp);
            this.add(mp);
            //显示
            this.setVisible(true);
            //注册监听
            this.addMouseMotionListener(mp);
            this.addMouseListener(mp);
            this.addKeyListener(this);
            //关闭新游戏和继续游戏按钮
            jmi1.setEnabled(false);
            jmi4.setEnabled(false);

        } else if (e.getActionCommand().equals("illustrate")) {
            //JFrame frame = new JFrame();
            final JDialog dialog = new JDialog(this, "提示", true);
            Font f1 = new Font("宋体", Font.BOLD, 18);
            JPanel jp = new JPanel();
            jp.setLayout(new GridLayout(1, 1));
            JTextArea jta = new JTextArea("控制:瞄准射击|R换弹|空格暂停|E切换开火模式\r\n加分:黄鸟5分|蓝鸟10分|红鸟可变金币\r\n      *点击金币可持续加分*\r\n\r\n无尽模式:鸟无限,子弹打完为止\r\n自选模式:可自选速度和并发数\r\n挑战模式:你懂的\r\n\r\n游戏最后会按金银铜评级\r\n此游戏纯属娱乐，版权问题概不负责", 20, 10);
            jta.setFont(f1);
            jp.add(jta);
            dialog.add(jp);
            dialog.setSize(410, 240);
            dialog.setResizable(false);
            dialog.setLocation(800, 430);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } else if (e.getActionCommand().equals("set")) {
            MyFrame myFrame = new MyFrame();
        } else if (e.getActionCommand().equals("close")) {
            try {
                if (ae != null) {
                    ae.stop();
                    ae = null;  
                }
                playMusic=false;
                //修改声音设置
                changeFile();
                
                
                
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else if (e.getActionCommand().equals("open1")) {
            try {
                if (ae != null) {
                    ae.stop();
                    ae = null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                ae = new AePlayWave("src/music/开始.wav");
                ae.start();
                playMusic=true;
                changeFile();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (e.getActionCommand().equals("open2")) {
            try {
                if (ae != null) {
                    ae.stop();
                    ae = null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                ae = new AePlayWave("src/music/重新开始.wav");
                ae.start();
                playMusic=true;
                changeFile();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (e.getActionCommand().equals("restart")) {
            //修补音乐bug,开启背景音乐按钮
            jmi8.setEnabled(true);
            jmi9.setEnabled(true);
            jmi3.setEnabled(true);
            MyFinishPanel.closeMusic();
            try {
                if(playMusic){
                ae = new AePlayWave("src/music/重新开始.wav");
                ae.start();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            //游戏难度设置
            Recorder.temp = 0;
            MyPanel.temp = true;
            Recorder.setAllbullet(90);
            Recorder.setBullet(10);
            Recorder.setScore(0);
            Recorder.setKillNum(0);
            //初始化自定义的值
            Recorder.setBdsNum(Init.getBdsNum());
            Changer.setSpeed(Init.getSpeed());
            Changer.setSrcNum(Init.getSrcNum());
            //初始化计数器
            account = 0;
            drawpic = 0;
            flag = false;
//******没有清除监听，开启多个画板会导致游戏的监听子弹减少加倍，此处偷懒********
            //mp=new MyPanel("newGame");
            // 启动mp线程
//       Thread t=new Thread(mp);
//        t.start();
            //先删除旧的面板
            this.remove(mfp);
            this.add(mp);

            //显示
            this.setVisible(true);
            //注册监听
//        this.addMouseMotionListener(mp);
//        this.addMouseListener(mp);
            //this.addKeyListener(this); 

            //mp.setFocusable(true);
            //Recorder.set
        }
    }
    @Override
    public void eventNeedClose(MyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("进入needclose");

        try {
            if (ae != null) {
                ae.stop();
                ae = null;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        mfp = new MyFinishPanel();
        //去掉旧的
        this.remove(mp);
        //换上新的
        this.add(mfp);
//        Thread t=new Thread(mfp);
//        t.start();
        //修补音乐bug,暂时关闭背景音乐按钮
        jmi8.setEnabled(false);
        jmi9.setEnabled(false);
        //关闭存盘退出
        jmi3.setEnabled(false);
        myjb = new JButton();
        mfp.setLayout(null);
        //myjb.setLocation(280,400);
        ImageIcon icon = new ImageIcon("src/image/restart.png");
        myjb.setIcon(icon);
        //myjb.setSize(213,74);
        //myjb.setPreferredSize(new Dimension(213, 74));
        myjb.setBounds(280, 400, 213, 77);
        mfp.add(myjb);
        //设置监听
        myjb.addActionListener(this);
        myjb.setActionCommand("restart");

        //显示
        this.setVisible(true);
    }

    //计数器测试变量
    //判断是暂停Space还是开始Space
    static int account = 0;
    //判断是否画出暂停图片
    static int drawpic = 0;
    //判断是否要account++，因为keypress监听里面有两个键值(R和Space)
    static boolean flag = false;
    
    //开火模式的计算变量
    static int account1=0;

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("进入监听");
//        System.out.println(account);
//        System.out.println(drawpic);
//        System.out.println(flag);
        if (e.getKeyCode() == KeyEvent.VK_R) {
            //System.out.println("换了子弹");
            try {
                AePlayWave as = new AePlayWave("src/music/reload.wav");
                as.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            //换子弹函数
            int temp = Recorder.getBullet();
            if (Recorder.getAllbullet()+Recorder.getBullet() >= 10) {
                Recorder.setBullet(10);
                Recorder.setAllbullet((Recorder.getAllbullet()+temp) - 10);
            } else {
                Recorder.setBullet(Recorder.getAllbullet()+Recorder.getBullet());
                Recorder.setAllbullet(0);
            }

        } else if (e.getKeyCode() == KeyEvent.VK_E) {
                if(account1%2==0)
                   mp.kaihuo=false;
                else
                   mp.kaihuo=true; 
                account1++;
        }else{
            //开火模式
            
            //游戏暂停，开始
            if ((e.getKeyCode() == KeyEvent.VK_SPACE) && (account % 2 == 0)) {
                for (int i = 0; i < mp.birds.size(); i++) {
                    mp.birds.get(i).setSpeed(0);
                }
                drawpic = 1;
                flag = true;
                
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE && account % 2 == 1) {
                for (int i = 0; i < mp.birds.size(); i++) {
                    mp.birds.get(i).setSpeed(Changer.getSpeed());
                }
                flag = true;
                drawpic = 2;
            }
            
        }
        if (flag) {
                account++;
            }
        flag=false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void changeFile(){
        //修改文件
            BufferedReader br1 = null;
            StringBuffer sb = null;
            BufferedWriter bw1 = null;
            //如果没有存档就新建存档
            File f = new File("D:\\myRecorder.ini");
            if (f.exists() == false) {
                try {
                    bw1 = new BufferedWriter(new FileWriter("D:\\myRecorder.ini"));
                    bw1.write(Integer.toString(Recorder.getBestScore())+ "\r\n");
                    bw1.write(playMusic+"\r\n");
                    System.out.println("修改文件");
                } catch (Exception e1) {

                } finally {
                    try {
                        bw1.close();
                    } catch (Exception e2) {

                    }
                }
            } else {

                //修改文件
                int lineDel = 1;
                try {
                    br1 = new BufferedReader(new FileReader("D:\\myRecorder.ini"));
                    sb = new StringBuffer(4096);
                    String temp = null;
                    int line = 0;
                    while ((temp = br1.readLine()) != null) {

                        if (line == lineDel) {
                            sb.append(playMusic).append("\r\n");
                            System.out.println("修改文件");
                        } else {
                            sb.append(temp).append("\r\n");
                        }
                        line++;
                    }
                    bw1 = new BufferedWriter(new FileWriter("D:\\myRecorder.ini"));
                    bw1.write(sb.toString());
                } catch (Exception e1) {
                } finally {
                    try {
                        br1.close();
                        bw1.close();
                    } catch (Exception e2) {
                    }
                }
            }
    }
}
//结算页面

class MyFinishPanel extends JPanel{

    Image image19 = null;
    Image image20 = null;
    Image backgroundImage = null;
    Image gameoverImage = null;
    Image imagegold = null;
    Image imagesilver = null;
    Image imagecopper = null;
    static AePlayWave asd = null;
//    JButton myjb=null;
    int play = 0;

    public MyFinishPanel () {
        if (play == 0) {
            try {
                asd = new AePlayWave("src/music/高分.wav");
                asd.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            //控制BGM
            if(myhitflybirdgame.playMusic)
            try {
                asd = new AePlayWave("src/music/game complete.wav");
                asd.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        play++;
    }
//使得外部调用关闭结算时的音乐

    public static void closeMusic() {
        try {
            if (asd != null) {
                asd.stop();
                asd = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void paint(Graphics g) {
        image19 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/score_panel.png"));
        image20 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/land.png"));
        backgroundImage = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bkground1.png"));
        gameoverImage = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/text_game_over.png"));
        imagegold = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/medals_1.png"));
        imagesilver = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/medals_2.png"));
        imagecopper = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/medals_3.png"));

        super.paint(g);
        
        //抗锯齿
        Graphics2D g2d=(Graphics2D)g; 
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        //背景图
        g.drawImage(backgroundImage, 0, 0, this);
        g.drawImage(image20, 0, 600, this);
        g.drawImage(image20, 336, 600, this);
        g.drawImage(image20, 672, 600, this);
        Font font = new Font("Lucida Console", Font.BOLD, 18);
        g.setFont(font);
        g.drawImage(gameoverImage, 288, 150, this);
        //面板
        g.drawImage(image19, 270, 220, this);
        //牌子，对玩家评级
        if (Recorder.getScore() <= 100) {
            g.drawImage(imagecopper, 300, 263, this);
        } else if ((Recorder.getScore() > 100) && (Recorder.getScore() <= 370)) {
            g.drawImage(imagesilver, 300, 263, this);
        } else if (Recorder.getScore() > 370) {
            g.drawImage(imagegold, 300, 263, this);
        }
        
        //音乐提示
//        if(myhitflybirdgame.playMusic){
//        g.drawString("BGM:ON", 710, 20);
//        }
//        else{   
//        g.drawString("BGM:OFF", 710, 20);
//        }
        
        //判断是否要修改最高分
        if (Recorder.getScore() > Recorder.getBestScore()) {
            //替换
            System.out.println("修改数值");
            Recorder.setBestScore(Recorder.getScore());
            BufferedReader br1 = null;
            StringBuffer sb = null;
            BufferedWriter bw1 = null;
            //如果没有存档就新建存档
            File f = new File("D:\\myRecorder.ini");
            if (f.exists() == false) {
                try {
                    bw1 = new BufferedWriter(new FileWriter("D:\\myRecorder.ini"));
                    bw1.write(Integer.toString(Recorder.getBestScore())+"\r\n");
                    bw1.write(Boolean.toString(myhitflybirdgame.playMusic));
                    System.out.println("修改文件");
                } catch (Exception e1) {

                } finally {
                    try {
                        bw1.close();
                    } catch (Exception e2) {

                    }
                }
            } else {

                //修改文件
                int lineDel = 0;
                try {
                    br1 = new BufferedReader(new FileReader("D:\\myRecorder.ini"));
                    sb = new StringBuffer(4096);
                    String temp = null;
                    int line = 0;
                    while ((temp = br1.readLine()) != null) {

                        if (line == lineDel) {
                            sb.append(Recorder.getScore()).append("\r\n");
                            System.out.println("修改文件");
                        } else {
                            sb.append(temp).append("\r\n");
                        }
                        line++;
                    }
                    bw1 = new BufferedWriter(new FileWriter("D:\\myRecorder.ini"));
                    bw1.write(sb.toString());
                } catch (Exception e1) {
                } finally {
                    try {
                        br1.close();
                        bw1.close();
                    } catch (Exception e2) {
                    }
                }
            }
        }
        //分数信息
        g.drawString(Recorder.getScore() + "", 441, 255);
        g.drawString(Recorder.getKillNum() + "", 441, 283);
        g.drawString(Recorder.getBestScore() + "", 441, 315);
//        myjb=new JButton();
//        myjb.setLocation(280,400);
//         ImageIcon icon = new ImageIcon("src/image/restart.png");
//         myjb.setIcon(icon);
//        myjb.setSize(213,74);
//        this.add(myjb);
        //暂时将子弹清空，防止枪声
        Recorder.setBullet(0);
        //设置监听
//       myjb.addActionListener(this);
//       myjb.setActionCommand("restart");
//        Recorder.setAllbullet(0);
    }

//    @Override
//    public void run() {
//        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        while (true){
//            try {
//                Thread.sleep(100);
//            } catch (Exception e) {
//            }
//            this.repaint();
//        }
//    }

}
//准备页面

class MyStartPanel extends JPanel implements Runnable {

    int times = 0;
    Image image19 = null;

    public void paint(Graphics g) {
        image19 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/text_ready.png"));
        super.paint(g);
        g.fillRect(0, 0, 800, 750);
        if (times % 2 == 0) {
            g.drawImage(image19, 300, 220, this);
        }
    }
//用Run函数刷新ready图片

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        while (true) {
            //休眠
            try {
                Thread.sleep(600);
            } catch (Exception e) {
                e.printStackTrace();
            }
            times++;
            //重画
            this.repaint();

        }
    }
}

//我的战斗面板
class MyPanel extends JPanel implements Runnable, MouseMotionListener, MouseListener {

    //定义一把枪
    Gun gun = null;
    //定义鸟队列
    ArrayList<Bird> birds = new ArrayList<Bird>();
    ArrayList<Node> nodes = new ArrayList<Node>();
    int birdsize = Changer.getSrcNum();
    //定义三张图片,三张图片才能组成一颗炸弹
    Image backgroundImage = null;
    Image image1 = null;
    Image image2 = null;
    Image image3 = null;
    Image image4 = null;
    Image image5 = null;
    Image image6 = null;
    Image image7 = null;
    Image image8 = null;
    Image image9 = null;
    Image image10 = null;
    Image image11 = null;
    Image image12 = null;
    Image image13 = null;
    Image image14 = null;
    Image image15 = null;
    Image image16 = null;
    Image image17 = null;
    Image image18 = null;
    Image image20 = null;
    Image image21 = null;
    Image image22 = null;
    Image image23 = null;
    Image image24 = null;
    Image image25 = null;
    Image image26 = null;
    Image image27 = null;
    Image image28 = null;
    Image image29 = null;
    //Image image30 = null;
    Image background1 = null;

    //构造函数
    public MyPanel(String flag) {
        gun = new Gun(300, 200,this);
        // 初始化图片
        image1 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird0_0.png"));
        image2 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird0_1.png"));
        image3 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird0_2.png"));
        image4 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird1_0.png"));
        image5 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird1_1.png"));
        image6 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird1_2.png"));
        image7 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird2_0.png"));
        image8 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird2_1.png"));
        image9 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird2_2.png"));

        image10 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird0_0_1.png"));
        image11 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird0_1_1.png"));
        image12 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird0_2_1.png"));
        image13 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird1_0_1.png"));
        image14 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird1_1_1.png"));
        image15 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird1_2_1.png"));
        image16 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird2_0_1.png"));
        image17 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird2_1_1.png"));
        image18 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bird2_2_1.png"));
        image20 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/land.png"));
        image21 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bullet_1.png"));
        image22 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/medals_1.png"));
        image23 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/killNum.png"));
        image24 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/medals_2.png"));
        image25 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/medals_3.png"));
        image26 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/button_resume.png"));
//        image27 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/button_pause.png"));
        image28 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/diedbird.png"));
        image29 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/diedbird1.png"));
        //image30 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/bullet.png"));

        background1 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/20180305_190359.jpg"));

        backgroundImage = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/image/20180306_222448.jpg"));
        //隐藏鼠标    
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.getImage("");
        Cursor cu = tk.createCustomCursor(img, new Point(10, 10), "stick");
        this.setCursor(cu);

        //初始化鸟群
        if (flag.equals("newGame")) {
            for (int i = 0; i < birdsize; i++) {
                int rd = (int) (Math.random() > 0.5 ? -50 : 805);
                int direct;
                if (rd < 0) {
                    direct = 0;
                } else {
                    direct = 1;
                }
                Bird bd = new Bird(rd, (int) (Math.random() * 400), direct, (int) (Math.random() * 2));
                birds.add(bd);
                Thread t = new Thread(bd);
                t.start();
                System.out.println(birds.size());
            }
        } else if (flag.equals("con")) {
            nodes = new Recorder().getScoreAndNodes();
            for (int i = 0; i < nodes.size(); i++) {

                Node node = nodes.get(i);
                Bird bd = new Bird(node.x, node.y, node.direct, node.color);
                birds.add(bd);
                Thread t = new Thread(bd);
                t.start();
                System.out.println(birds.size());
            }
        }

    }

    //画出提示信息
    public void showInfo(Graphics g) {
        Font font = new Font("Lucida Console", Font.BOLD, 18);
        g.setFont(font);
        //显示鸟数
        g.drawImage(image1, 105, 615, this);
        g.drawString(Recorder.getBdsNum() + "", 155, 645);
        //显示子弹数
        g.drawImage(image21, 110, 655, 30, 30, this);
        //开火模式显示
        if(kaihuo){
            font = new Font("微软雅黑", Font.BOLD, 18);
        g.setFont(font);
            g.drawString("开火模式：单发", 320, 645);
            font = new Font("Lucida Console", Font.BOLD, 18);
        g.setFont(font);
        }else{
            font = new Font("微软雅黑", Font.BOLD, 18);
        g.setFont(font);
            g.drawString("开火模式：全自动", 320, 645);
            font = new Font("Lucida Console", Font.BOLD, 18);
        g.setFont(font);
        }
        if (Recorder.getBullet() == 0) {
            g.setColor(Color.red);
            g.drawString("Press R to Reload", 295, 675);
        } else {
            g.setColor(Color.black);
        }
        g.drawString(Recorder.getBullet() + "", 155, 680);
        g.setColor(Color.black);
        g.drawString("/", 178, 680);
        g.drawString(Recorder.getAllbullet() + "", 190, 680);
        //显示分数
        if (Recorder.getScore() <= 100) {
            g.drawImage(image25, 600, 625, 32, 32, this);
        } else if ((Recorder.getScore() > 100) && (Recorder.getScore() <= 370)) {
            g.drawImage(image24, 600, 625, 32, 32, this);
        } else if (Recorder.getScore() > 370) {
            g.drawImage(image22, 600, 625, 32, 32, this);
        }
        g.drawString(Recorder.getScore() + "", 645, 645);
        //显示击杀
        g.drawImage(image23, 600, 660, 35, 35, this);
        g.drawString(Recorder.getKillNum() + "", 645, 683);
    }
//重写paint

    public void paint(Graphics g) {
        super.paint(g);
//抗锯齿
        Graphics2D g2d=(Graphics2D)g; 
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //背景图
        g.drawImage(backgroundImage, 0, 0, this);
        g.drawImage(image20, 0, 600, this);
        g.drawImage(image20, 336, 600, this);
        g.drawImage(image20, 672, 600, this);
        
        //speed up修改背景
        if (Recorder.getBdsNum() <= 30) {
            Font font = new Font("Lucida Console", Font.BOLD, 24);
            g.setFont(font);
            g.drawImage(background1, 0, 0, this);
            g.drawString("Speed Up", 339, 50);
        }
        drawBlood(g);
        //音乐提示
        if(myhitflybirdgame.playMusic){
        Font font = new Font("Lucida Console", Font.PLAIN, 18);
        g.setFont(font);
        g.drawString("BGM:ON", 710, 20);
        }
        else{
        Font font = new Font("Lucida Console", Font.PLAIN, 18);
        g.setFont(font);    
        g.drawString("BGM:OFF", 710, 20);
        }
        
        //画出提示信息
        this.showInfo(g);
        //画鸟
        for (int i = 0; i < birds.size(); i++) {

            Bird bd = birds.get(i);
            drawBird(bd, g);
        }
        if (birds.size() < Changer.getSrcNum()) {
            int rd = (int) (Math.random() > 0.5 ? -50 : 805);
            int direct;
            if (rd < 0) {
                direct = 0;
            } else {
                direct = 1;
            }
            Bird bd = new Bird(rd, (int) (Math.random() * 400), direct, (int) (Math.random() * 3));
            drawBird(bd, g);
            birds.add(bd);
            Thread t = new Thread(bd);
            t.start();
            //System.out.println("2."+birds.size());
        }
        //画枪
        if (myhitflybirdgame.drawpic != 1) {
            this.drawGun(gun.x, gun.y, g2d);
        }
        //暂停
        if (myhitflybirdgame.drawpic == 1) {
            g.drawImage(image26, 360, 300, this);
        }
    }

    public void drawGun(int x, int y, Graphics2D g) {
        if (Recorder.getBullet() == 0) {
            g.setStroke(new BasicStroke(1.6f));
            g.setColor(Color.red);
        } else {
            g.setStroke(new BasicStroke(1.6f));
            g.setColor(Color.black);
        }

        g.drawLine(x - 3, y + 15, x + 12, y + 15);
        g.drawLine(x + 18, y + 15, x + 33, y + 15);
        g.drawLine(x + 15, y - 3, x + 15, y + 12);
        g.drawLine(x + 15, y + 18, x + 15, y + 33);
        g.drawOval(x, y, 30, 30);
        g.drawOval(x + 7, y + 7, 15, 15);
        g.drawOval(x + 15, y + 15, 0, 0);
    }
    
    public void drawBlood(Graphics g){
        g.fillRect(0, 15, Recorder.getBdsNum()*5, 5);
    }

    public void drawBird(Bird bd, Graphics g) {
        if (bd.isLive) {
            switch (bd.color) {
                //黄色
                case 0:
                    switch (bd.direct) {
                        //向右
                        case 0:
                            if (bd.life % 3 == 2) {
                                g.drawImage(image1, bd.x, bd.y, this);
                                //System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 1) {
                                g.drawImage(image2, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 0) {
                                g.drawImage(image3, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            }
                            break;
                        //向左
                        case 1:
                            if (bd.life % 3 == 2) {
                                g.drawImage(image10, bd.x, bd.y, this);
                                //System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 1) {
                                g.drawImage(image11, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 0) {
                                g.drawImage(image12, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            }
                            break;
                    }
                    break;
                case 1:
                    //蓝色
                    switch (bd.direct) {
                        case 0:
                            //向右
                            if (bd.life % 3 == 2) {
                                g.drawImage(image4, bd.x, bd.y, this);
                                //System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 1) {
                                g.drawImage(image5, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 0) {
                                g.drawImage(image6, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            }
                            break;
                        case 1:
                            if (bd.life % 3 == 2) {
                                g.drawImage(image13, bd.x, bd.y, this);
                                //System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 1) {
                                g.drawImage(image14, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 0) {
                                g.drawImage(image15, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            }
                            break;
                    }
                    break;
                case 2:
                    switch (bd.direct) {
                        case 0:
                            if (bd.life % 3 == 2) {
                                g.drawImage(image7, bd.x, bd.y, this);
                                //System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 1) {
                                g.drawImage(image8, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 0) {
                                g.drawImage(image9, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            }
                            break;
                        case 1:
                            if (bd.life % 3 == 2) {
                                g.drawImage(image16, bd.x, bd.y, this);
                                //System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 1) {
                                g.drawImage(image17, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            } else if (bd.life % 3 == 0) {
                                g.drawImage(image18, bd.x, bd.y, this);
                                // System.out.println("hitflybird.MyPanel.paint()");

                            }
                            break;
                    }
                    break;
            }
            //让bd的生命值减小
            bd.lifedown();
            //System.out.println(birds.size());

        } else if (bd.isLive == false && bd.isNeed && bd.y <= 560) {
            //死了就不动,变黑
            switch (bd.direct) {
                case 0:
                    switch(bd.color){
                        case 0:
                        case 1:
                            g.drawImage(image28, bd.x, bd.y, this);
                            break;
                        case 2:
                            g.drawImage(image22, bd.x, bd.y,35,35, this);
                            break;
                    }
                    //g.drawImage(image22, bd.x, bd.y, this);
                    break;
                case 1:
                    switch(bd.color){
                        case 0:
                        case 1:
                            g.drawImage(image29, bd.x, bd.y, this);
                            break;
                        case 2:
                            g.drawImage(image22, bd.x, bd.y,35,35, this);
                            break;
                    }
                    //g.drawImage(image29, bd.x, bd.y, this);
                    break;
            }
        } else {
            //如果鸟的生命值等于零就去掉
            birds.remove(bd);
            //System.out.println(birds.size());
        }
    }

    static boolean temp = true;
    //开火模式
    static boolean kaihuo=true;

    @Override
    public void run() {
        //每隔20毫秒去重绘，间隔越短越连贯

        while (true) {
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //判断是否暂停
            //重绘

            this.repaint();
            //实现游戏节奏加快
            if (Recorder.getBdsNum() <= 30 && temp == true) {
                Changer.setSpeed(Changer.getSpeed() + 3);
                //该变量控制游戏只加速一次,再次游戏后需要初始化
                temp = false;
                System.out.println("游戏加速");
            }

        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
//        gun.x = e.getX()- 18;
//        gun.y = e.getY()- 62;
        gun.x = e.getX() - 24;
        gun.y = e.getY() - 45;
        repaint();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        gun.x = e.getX() - 18;
//        gun.y = e.getY()- 62;   
          gun.x = e.getX() - 24;
          gun.y = e.getY() - 45;
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
Thread reduce=null;
//是否能够击杀
boolean kill=false;
    @Override
    public void mousePressed(MouseEvent e) {
        if (Recorder.getBullet() == 0) {
            System.out.println("请按R换子弹");
        } else {

//            try {
//                AePlayWave as = new AePlayWave("src/music/shot.wav");
//                as.start();
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
            //子弹减少
         if(!kaihuo)
         {
             //全自动
         reduce=new Thread(gun);
         kill=true;
         reduce.start();
         }else{
             //单发
             try {
                AePlayWave as = new AePlayWave("src/music/shot.wav");
                as.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
             Recorder.reduceBullet();
             killBird(gun.x, gun.y, birds);
         }
            //Recorder.reduceBullet();
         //killBird(e.getX(), e.getY(), birds);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if(!kaihuo&&reduce!=null){
        reduce.stop();
        
        }
        kill=false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void killBird(int x,int y,ArrayList<Bird> birds){
        for (int i = 0; i < birds.size(); i++) {

                Bird bd = birds.get(i);

                if (x >= (bd.x-15)  && x <= (bd.x + 33) && y >= (bd.y-15)  && y <= (bd.y + 33) && (bd.isLive||bd.tomedal)) {
                    Recorder.addkillNum();
                    bd.isLive = false;
                    bd.gunkill = true;
                    Recorder.reduceBirdNum();
                    //击杀后的奖励分类
                    switch (bd.color) {
                        //杀黄鸟
                        case 0:
                            Recorder.addscore();
                            break;
                        //杀蓝鸟
                        case 1:
                            Recorder.addextrascore();
                            break;
                        //杀红鸟
                        case 2:
                            Recorder.addBullet();
                            Recorder.addBdsNum();
                            Recorder.addscore();
                            bd.tomedal=true;
                            break;
                    }

                    //System.out.println(bd.isNeed);
                    try {
                        AePlayWave as = new AePlayWave("src/music/hit.wav");
                        TimeUnit.MILLISECONDS.sleep(90);
                        as.start();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    this.repaint();
                }
            }
    }
    
}
