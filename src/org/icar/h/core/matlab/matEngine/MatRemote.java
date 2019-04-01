package org.icar.h.core.matlab.matEngine;

import com.mathworks.engine.EngineException;

import java.rmi.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public interface MatRemote extends Remote {

     void print_hello() throws RemoteException,InterruptedException, ExecutionException;
     void startEngine() throws RemoteException,InterruptedException, ExecutionException;
     HashMap<String,Double> evaluateSolution(ArrayList<String> switchers, ArrayList<String> all_switchers , ArrayList<String> open_switchers, int num_loads ) throws CancellationException, EngineException, IllegalStateException, InterruptedException, ExecutionException,RemoteException;



}
