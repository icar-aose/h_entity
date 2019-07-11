function result = checkallGenerators(mg1, auxg1)

mg1Eval = checkGenerator(mg1, 0.600);
auxg1Eval = checkGenerator(auxg1, 0.400);

result = mg1Eval && auxg1Eval ;
end