function execute(model)
    t1 = datetime('now');
    sim(model);
    generalStatus = checkallGenerators(mg1, auxg1);

    assignin('base', 'mg1', mg1);
    assignin('base', 'auxg1', auxg1);
    assignin('base', 'load1', load1);
    assignin('base', 'load2', load2);
    assignin('base', 'load3', load3);
    assignin('base', 'load4', load4);
    
    assignin('base', 'genResult', generalStatus);

    t2 = datetime('now');
    disp(strcat('Total time: ', datestr(t2-t1, 'HH:MM:SS')));

end