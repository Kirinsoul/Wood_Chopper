/**
 * https://osbot.org/api/
 */

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;


@ScriptManifest(author = "LRDBLK", info = "Chops wood", name = "Wood Chopper", version = 1, logo = "")
public class WoodCutter extends Script {

    /////////////
    //VARIABLES//
    ////////////
    //private final Area[] BANKS = {Banks.LUMBRIDGE_UPPER, Banks.VARROCK_EAST, Banks.VARROCK_WEST, Banks.EDGEVILLE, Banks.GRAND_EXCHANGE, Banks.DRAYNOR};
    //private final Area LUM_TREES = new Area(3176, 3238, 3200, 3207);
    //private final Area LUM_BANK = Banks.LUMBRIDGE_UPPER;
    private String treeType;
    private Area chopArea;
    private Area bankArea;
    private String state;
    private int gainedExp;
    private boolean shouldStart = false; //start script after button press
    private boolean shouldBank = true; //should script bank

    //script state
    private enum State{
        CHOP, BANK, TRAVEL_TO, TRAVEL_BACK, WAIT, DROP
    }

    //tree info for GUI
    public enum TreeInfo{

        TREE("Tree",  new String[]{"Lumbridge"}),
        OAK("Oak",  new String[]{"Varock East", "Varock West"}),
        WILLOW("Willow", new String[]{"Draynor Village"});

        private String treeType;
        private String[] treeLocation;

        TreeInfo(String s, String[] l){

            treeType = s;
            treeLocation = l;
        }

        public String getTreeType(){
            return treeType;

        }

        public String[] getTreeLocation(){
            return treeLocation;
        }

    }


    public enum AreaInfo{

        LUMBRIDGE("Lumbridge", Banks.LUMBRIDGE_UPPER, new Area(3175, 3240, 3200, 3207)),
        VAROCK_EAST("Varock East", Banks.VARROCK_EAST, new Area(3239, 3373, 3273, 3358)),
        VAROCK_WEST("Varock West", Banks.VARROCK_WEST, new Area(3157, 3418, 3173, 3369)),
        DRAYNOR("Draynor Village", Banks.DRAYNOR, new Area(3078, 3243, 3092, 3226));

        private String location;
        private Area bankArea;
        private Area choppingArea;

        AreaInfo(String l, Area a, Area b){
            location = l;
            bankArea = a;
            choppingArea = b;
        }

        public String getLocation() {
            return location;
        }

        public Area getBankArea() {
            return bankArea;
        }

        public Area getChoppingArea() {
            return choppingArea;
        }
    }

    private Predicate<RS2Object> suitableTree = tree ->
            tree != null &&
            chopArea.contains(tree) &&
            tree.getName().equalsIgnoreCase(treeType) &&
            tree.hasAction("Chop down") &&
            peopleAroundEntity(tree, 2) <= 3;

    private Predicate<RS2Object> secondBestTree = tree ->
            tree != null &&
            chopArea.contains(tree) &&
            tree.hasAction("Chop down") ;

    private int peopleAroundEntity (Entity e, int areaSize){
        return toIntExact(players.getAll().stream().filter(x -> e.getArea(areaSize).contains(x)).count());
    }




    /////////////
    //ACCESSORS//
    ////////////


    ////////////
    //MUTATORS//
    ///////////

    void setShouldStart(boolean shouldStart) {
        this.shouldStart = shouldStart;
    }

    void setShouldBank(boolean shouldBank) {
        this.shouldBank = shouldBank;
    }

    void setTreeChop(String treeChop) {
        this.treeType = treeChop;
    }

    void setChopArea(Area chopArea) {
        this.chopArea = chopArea;
    }

    void setBankArea(Area bankArea) {
        this.bankArea = bankArea;
    }

    //////////////////////
    // REQUIRED METHODS //
    //////////////////////


    @Override
    public void onStart() {
        GuiMain gui = new GuiMain(this);
        gui.setVisible(true);
        log("Script is starting!");

    }

    @Override
    public int onLoop() throws InterruptedException {

        if (shouldStart) {

            switch(getState()){
                case CHOP:
                    state = "Chopping tree";
                    java.util.List<RS2Object> tree = getObjects().getAll().stream().filter(suitableTree).collect(Collectors.toList());
                    gainedExp = getExperienceTracker().getGainedXP(Skill.WOODCUTTING);
                    if (tree != null){
                        tree.sort(Comparator.<RS2Object>comparingInt(a -> getMap().realDistance(a))
                                .thenComparingInt(b -> getMap().realDistance(b)));
                        log("Currently Chopping: " + tree.get(0).getName());
                        log(" Amount of players around tree: "+ peopleAroundEntity(tree.get(0), 2));
                        if (tree.get(0).interact("Chop down")){
                            Timing.waitCondition(() -> gainedExp != getExperienceTracker().getGainedXP(Skill.WOODCUTTING)
                                    , random(600, 2000));
                        }
                    }else{
                        log("Trees have a lot of players around, consider using another world");
                        tree = getObjects().getAll().stream().filter(secondBestTree).collect(Collectors.toList());
                        tree.sort(Comparator.<RS2Object>comparingInt(a -> peopleAroundEntity(a, 2))
                                .thenComparingInt(b -> peopleAroundEntity(b, 2)));
                        log("Best tree has " + peopleAroundEntity(tree.get(0), 2) + " people around it");
                        if (tree.get(0).interact("Chop down")){
                            Timing.waitCondition(() -> gainedExp != getExperienceTracker().getGainedXP(Skill.WOODCUTTING)
                                    , random(350, 1200));
                        }

                    }
                    //RS2Object tree = getObjects().closest(chopArea, treeType);
                    //if(tree != null ) chopTree(tree);
                    //getCamera().toEntity(tree);
                    break;

                case TRAVEL_TO:
                    state = "Walking to bank";
                    getWalking().webWalk(bankArea);
                    break;

                case BANK:
                    state = "Banking !";
                    log("depositing");
                    depositBank();
                    break;

                case TRAVEL_BACK:
                    state = "Walking to trees";
                    walking.webWalk(chopArea);
                    break;

                case DROP:
                    state = "Dropping logs";
                    log("dropping everything");
                    inventory.dropAllExcept(item -> item.getName().endsWith(" axe"));
                    break;

                case WAIT:
                    state = "Waiting";
                    getMouse().moveOutsideScreen();
                    Timing.waitCondition(() -> canWork(), random(500, 900));
            }
        }

        return random(50, 300);
    }

    @Override
    public void onExit() {
        log("RawR");
    }

    @Override
    public void onPaint(Graphics2D g) {

        Point mP = getMouse().getPosition();

        //mouse X
        g.drawLine(mP.x - 5, mP.y + 5, mP.x + 5, mP.y - 5);
        g.drawLine(mP.x + 5, mP.y + 5, mP.x - 5, mP.y - 5);

        g.setColor(Color.white);

        Font normal = new Font("SANS_SERIF", Font.BOLD, 14);
        Font italic = new Font("SANS_SERIF", Font.ITALIC, 12);
        g.setColor(Color.WHITE);
        g.setFont(normal);

        g.drawString("LRDBLK's Wood Cutter !", 8, 30);
        g.drawString("Time Elapsed: " + Timing.msToString(experienceTracker.getElapsed(Skill.WOODCUTTING)), 8, 45);
        g.drawString("Woodcutting Exp Per hour: " + experienceTracker.getGainedXPPerHour(Skill.WOODCUTTING) + "per/hour", 8, 60);
        g.drawString("Woodcutting Levels Gained: " + getExperienceTracker().getGainedLevels(Skill.WOODCUTTING), 8, 75);
        g.setFont(italic);
        g.drawString(state, 8, 90);



    }



    //////////////////////
    // CUSTOM METHODS //
    //////////////////////

    /*
       Enter a tree name
       Returns the location of that tree
     */
    public String[] findTreeLocation(String s){
        for (TreeInfo t : TreeInfo.values()){
            if(t.getTreeType().contains(s)){
                return t.getTreeLocation();
            }
        }
        return null;
    }

    /*
        Returns a String array of all tree names
     */
    public String[] getTrees(){
        LinkedList l = new LinkedList();
        for(TreeInfo t : TreeInfo.values()){
            l.add(t.getTreeType());
        }
        return Arrays.copyOf(l.toArray(), l.toArray().length, String[].class);
    }

    /*
        Enter a location
        Returns: the area where the trees are for that location
     */

    public Area getChopArea(String c){
        for (AreaInfo a : AreaInfo.values()){
            if(a.getLocation().equalsIgnoreCase(c)){
                return a.getChoppingArea();
            }
        }
        return null;
    }

    /*
        Enter the location
        Returns the bank for that area
     */

    public Area getBankArea(String c){
        for (AreaInfo a : AreaInfo.values()){
            if(a.getLocation().equalsIgnoreCase(c)){
                return a.getBankArea();
            }
        }
        return null;
    }

    /*
        Usage: get the current state of the player
     */
    private State getState() throws InterruptedException{
        if (shouldBank){
            if (canWork()) {
                if (inventory.isFull()){
                    if (bankArea.contains(myPlayer())){
                        return State.BANK;
                    }else{
                        return State.TRAVEL_TO;
                    }
                }else{
                    if (chopArea.contains(myPlayer())){
                        return State.CHOP;
                    }else{
                        return State.TRAVEL_BACK;
                    }
                }
            }

        }else{
            if (canWork()) {
                if (inventory.isFull()){
                    return State.DROP;
                }else{
                    return State.CHOP;
                }
            }

        }

        return State.WAIT;

    }

    /*
        Usage: checks if player is able to start a new action
     */

    private boolean canWork() throws InterruptedException{
        for(int i = 0; i <4; i++){
            if(!myPlayer().isAnimating() && !myPlayer().isMoving()){
                return true;
            }
            sleep(random(200, 350));
        }
        return false;
    }

    /*
        Usage: opens bank, deposits everything
     */
    private void depositBank() throws InterruptedException{

        log("banking!");
        if(!getBank().isOpen()){
            getBank().open();
            Timing.waitCondition(() -> getBank().isOpen(), 5000);
        }else{
            getBank().depositAllExcept(item -> item.getName().endsWith(" axe"));

        }

    }



}