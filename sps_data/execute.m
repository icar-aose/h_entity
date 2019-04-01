function execute(model)
    t1 = datetime('now');
    sim(model);
    generalStatus = checkallGenerators(mg1, mg2, auxg1, auxg2);
    assignin('base', 'mg1', mg1);
    assignin('base', 'mg2', mg2);
    assignin('base', 'auxg1', auxg1);
    assignin('base', 'auxg2', auxg2);
  
    assignin('base', 'load1', load1);
    assignin('base', 'load2', load2);
    assignin('base', 'load3', load3);
    assignin('base', 'load4', load4);
    assignin('base', 'load5', load5);
    assignin('base', 'load6', load6);
    assignin('base', 'load7', load7);
    assignin('base', 'load8', load8); 
    assignin('base', 'load9', load9);
    assignin('base', 'load11', load11);
    assignin('base', 'load12', load12);
    assignin('base', 'load13', load13);
    assignin('base', 'load14', load14);
    assignin('base', 'load15', load15);
    assignin('base', 'load16', load16);
    assignin('base', 'load17', load17);
    assignin('base', 'load18', load18);
    assignin('base', 'load19', load19);
    assignin('base', 'load21', load21);
    assignin('base', 'load22', load22);
    assignin('base', 'load23', load23);
    assignin('base', 'load24', load24);
    
    assignin('base', 'genResult', generalStatus);
    t2 = datetime('now');
    disp(strcat('Total time: ', datestr(t2-t1, 'HH:MM:SS')));

end