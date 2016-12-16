
package smartcollision;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class SmartCollision extends JFrame{   //截至到2015年11月21日，本程序完成，不再做大幅度修改-ERON
    //重新更改架构，将船舶自身的动态属性和静态属性分开
    //比如船舶的位置可以放在ship类中，但是操纵性能    ，    风流信息的影响，   避碰规则应该以导入的方式注入船舶
    //增加系统的粘度，增加可更改性能
    public static void main(String[] args) {
        //initialing
        JFrame frame = new JFrame();
        Show show = new Show();
        SmartCollision smartCollision = new SmartCollision();
        //setup frame and add panel
        frame.setSize(1100, 730);
        frame.add(show);
        frame.setTitle("ONE STEP V2");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        while(!DataBase.begin){
            while (!DataBase.pause){
                show.repaint();
                for(Ship b: DataBase.ships){
                    b.goAhead();
                }
                smartCollision.Action();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SmartCollision.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Logger.getLogger(SmartCollision.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        while(DataBase.begin){
            //show.repaint();
            show.paintTrack(DataBase.g);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(SmartCollision.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        DataBase.ships.clear();
        frame.dispose();
        System.exit(0);
    }
    
    //2016.12.10记：这个方法不再使用，更新数据放在船舶内部，每次调用即可
    public void Action(){
        analyse();
        //Action and reset
        if(!DataBase.ships.isEmpty()&&DataBase.ships.getLast().Action!=0) DataBase.danger = true;
        else DataBase.danger = false;
        for (Iterator<Ship> it = DataBase.ships.iterator(); it.hasNext();) {
            Ship dyship = it.next();
            //            switch(dyship.Action){
//                case 1:{
//                    dyship.giveValue(3, dyship.getParameter(3)+2);
//                    //dyship.Action = 0;
//                    break;
//                }
//                case 2:{
//                    dyship.giveValue(3, dyship.getParameter(3)-2);
//                    //dyship.Action = 0;
//                    break;
//                }
//                case 3:{
//                    dyship.giveValue(4, dyship.getParameter(4)-2);
//                    //dyship.Action = 0;
//                    break;
//                }
//                case 4:{
//                    dyship.giveValue(4, dyship.getParameter(4)+2);
//                   //dyship.Action = 0;
//                    break;
//                }
//                //
//            }
            dyship.shipDatachange();
            //清除数据，重新计算，   效率低下
            dyship.dangerList.clear();
            dyship.dataList.clear();
            dyship.danger = false;
        } //remove();
    }
    
    public void analyse(){
        //danger area add to arealist
        for(Ship boat: DataBase.ships){
            int index = 0;
            for(Ship ship: DataBase.ships){
                if(boat!=ship){
                    if(Math.abs(boat.getParameter(1)-ship.getParameter(1))<200&&
                            Math.abs(boat.getParameter(2)-ship.getParameter(2))<200){
                        boat.dangerList.add(ship);
                        boat.danger = true;  //本船危险标志
                    }
                }
            }
            
            for(int i = 0;i<boat.dangerList.size();i++){
                Ship ship = boat.dangerList.get(i);
                double boatx = boat.getParameter(1);
                double boaty = boat.getParameter(2);
                double bc = boat.getParameter(4);  //当前船的方向
                double shipx = ship.getParameter(1);
                double shipy = ship.getParameter(2);
                double rc = DataBase.CaculateRatio(boatx, boaty, shipx, shipy);
                double rp = rc - bc;
                if(rp>180) rp = rp - 360;//port - // starboard + //limit 0-180
                if(rp<-180) rp = 360 + rp;
                boat.dataList.add(i, rp);
                //analyse rp multiple
            }
            //rp信息存储在了datalist上，根据i索引读取
            for(int i = 0; i < boat.dataList.size(); i++){
//                if(boat.dataList.get(i)>292.5||boat.dataList.get(i)<60)
//                    index = 4;
//                else if(boat.dataList.get(i)>90&&boat.dataList.get(i)<150)
//                    index = 3;
                double temp = boat.dataList.get(i);    //避碰规则需要修改
                if (temp>-10&&temp<10) {
                    index = 5;
                }else if (temp>10&&temp<30) {
                    index = (int) Math.abs(temp);
                }else if (temp>30&&temp<67.5) {
                    index = 30;
                }else if (temp>67.5&&temp<112.5) {
                    index = -5;
                }else{
                    index = 0;
                }
            }
            boat.Action = index;
            //boat.dangerList.clear();
            //boat.dataList.clear();
        }
        
    }
    
    public void remove(){
        for(int i = 0;i<DataBase.ships.size();i++){
            Ship boat = DataBase.ships.get(i);
            double boatx = boat.getParameter(1);
            double boaty = boat.getParameter(2);
            for(int j = 0;j<boat.dangerList.size();j++){
                Ship ship = boat.dangerList.get(j);
                double shipx = ship.getParameter(1);
                double shipy = ship.getParameter(2);
                if(Math.abs(boatx-shipx)>200 && Math.abs(boaty-shipy)>200){
                    boat.dangerList.remove(j);
                }
            }
        }
    }
    
}
