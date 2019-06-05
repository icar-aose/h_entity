function result = checkallGenerators(mg1, auxg1)

mg1Eval = checkGenerator(mg1, 1200);
auxg1Eval = checkGenerator(auxg1, 400);

result = mg1Eval && auxg1Eval ;
end