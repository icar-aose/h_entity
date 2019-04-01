function result = checkallGenerators(mg1, mg2, auxg1, auxg2)
  
    mg1Eval = checkGenerator(mg1, 1200);
    mg2Eval = checkGenerator(mg2, 1200);
    auxg1Eval = checkGenerator(auxg1, 400);
    auxg2Eval = checkGenerator(auxg2, 400);
    
    result = mg1Eval && mg2Eval && auxg1Eval && auxg2Eval;
end