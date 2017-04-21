package org.inaetics.dronessimulator.drone;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;



import org.inaetics.dronessimulator.common;
/**
 * Represents a drone in the system.
 */
abstract class Drone implements MessageHandler{
    private volatile Publisher m_publisher;
    private volatile Subscriber m_subscriber;

    private D3Vector position;
    private D3Vector velocity;
    private D3Vector acceleration;

    /** --- CONSTRUCTOR */
    public Drone() {
        this(new D3Vector());
    }

    public Drone(D3Vector postion){
        this.position = position;
        this.velocity = new D3Vector();
        this.acceleration = new D3Vector();
    }


    public init(){
        this.m_subscriber.addHandler(StateMessage.class, this);
        try {
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
        }
    }

    /**
    * -- GETTERS
     */
    public D3Vector getPostion(){
        return position;
    }

    public D3Vector getVelocity(){
        return velocity;
    }

    public D3Vector getAcceleration(){
        return acceleration;
    }

    /**
     * -- SETTERS
     */
    private void setPosition(D3Vector new_position){
            position = new_position;
    }

    private void setVelocity(D3Vector new_velocity){
            velocity = new_velocity;
    }

    private void setAcceleration(D3Vector new_acceleration){
            acceleration = new_acceleration;
    }

    /**
     * -- FUNCTIONS
     * */
    private abstract recalculatAcceleration();


    private void calculateTactics(){
        this.recalculatAcceleration();
        this.sendTactics();
    }

    private synchronized void sendTactics(){
        MovementMessage msg = new MovementMessage();
        //msg.setDirection();
        msg.setAcceleration(this.getAcceleration());

        try {
            m_publisher.send(MessageTopic.MOVEMENTS, msg);
        } catch (IOException e) {
        }
    }

    public synchronized void handleMessage(Message message) {
        StateMessage stateMessage = (StateMessage) message;
        if (stateMessage.getPosition().isPresent()) this.setPosition(stateMessage.getPosition().get());
        if (stateMessage.getDirection().isPresent()) this.setDirection(stateMessage.getDirection().get());
        if (stateMessage.getAcceleration().isPresent()) this.setAcceleration(stateMessage.getAcceleration().get());
        this.calculateTactics();
    }






}