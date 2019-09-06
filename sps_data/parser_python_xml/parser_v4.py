import xml.etree.ElementTree as ET
import string
import re
import sys

def num_node(elem):
	return int(elem[4:])


def sabatucci (tagB , node, numnode, bool_node ):				#dobbiamo scorrere tutti i branch, prima il caso semplice, poi gli altri
	childB = tagB.findall('Branch')	
	if(len(childB)==0):								#caso semplice, se e' 0 vuol dire che ho finito i branch
			tagPinB = tagB.findall('P')     			#solo i P della i esima line all'interno dell'i esimo branch
			for P in tagPinB:
				if(P.get('Name')=='SrcBlock' or P.get('Name')=='DstBlock'):
					P=P.text.replace('\n', '')
					load=P.replace(' ', '')
					node.append(load+" nodo"+str(numnode))
			return node,bool_node;
	else:    #ho ancora dei branch quindi un nodo, che scrivo nel file
		numnode_succ=bool_node.index(0)
		node.append("nodo"+str(numnode)+" nodo"+str(numnode_succ))
		numnode=numnode_succ
		bool_node[numnode]=1
		for B in childB:					#per ogni B deve essere associato il nodo di appartenenza che e' numnode
			node,bool_node=sabatucci(B, node, numnode_succ,bool_node)
		return node,bool_node;

def main():
	Sorgente = ''
	Destinazione = ''
	#out_file = open("fase1.txt","w")
	bool_node=[1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
				0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]   #il v4 presenta la modifica della lista booleana dei nodi utilizzati 
	numnode=0
	node=[]
	lines=[]
	if (sys.argv[1]!=''):
		tree = ET.parse(sys.argv[1])   #passo argomento dall'esterno
		root = tree.getroot()	
		System = root.find ('Model/System')
		for line in System.findall('Line'):          #scorro i tag all'interno delle varie linee, ma solo i figli (primo grado)
			tagP = line.findall('P')		 		#solo i P della i esima line primo livello
			tagB = line.findall('Branch')	        #solo i branch della i esima line primo livello
			for elem in tagP:
				if(elem.get('Name')=='SrcBlock'):
						Sorgente = elem.text.replace('\n', '')
						Sorgente=Sorgente.replace(' ', '')
				if(elem.get('Name')=='DstBlock'):
				 		Destinazione = elem.text.replace('\n', '')
				 		Destinazione = Destinazione.replace(' ','')
			if (Destinazione=='' and Sorgente!=''):    #se non c'e' destinazione e' chiaro che ci sia un branch quindi un nodo
				numnode=bool_node.index(0)
				node.append(Sorgente+" nodo"+str(numnode))
				bool_node[numnode]=1
				for B in tagB:
					node,bool_node=sabatucci(B , node, numnode,bool_node)     # i branch dello stesso livello sono collegati allo stesso "nodo"
						

			elif(Sorgente=='' and Destinazione!=''):
				Sorgente=Destinazione
				numnode=bool_node.index(0)
				node.append(Sorgente+" nodo"+str(numnode))
				bool_node[numnode]=1
				for B in tagB:
					node,bool_node=sabatucci(B , node, numnode,bool_node)

			else:
				node.append(Sorgente+","+Destinazione)
				#out_file.write(Sorgente)
				#out_file.write(","+Destinazione)
			#if(len(node)!=0):
			#out_file.write(node[0])
			#node=node[1:]
			#for i in node:
			#	if(i.index(0)):
			#		out_file.write(i)
			#	out_file.write(","+i)
			#out_file.write("\n" )	
			
			lines.append(node)
			
			Destinazione=''
			Sorgente=''
			node=[]

		#print(bool_node.index(0))

		#out_file.close()

		#a questo punto modifichiamo l'array di liste che contiene le linee

		filtro=[]
		# print lines
		# print("\n")

		for line in lines:
			val=0
			for elems in line:
					if (len(line)==1):
						elem=elems.split(",")
						for el in elem:
							#print el
			 				if(re.match('G[0-9]', el)!=None ):    #devo splittare gli elementi con il comando split e la virgola, dopodiche
			 					val=-1  							#elimino l'elemento 

					else:	
						elem=elems.split(" ")
			 			for el in elem:
			 				if(re.match('G[0-9]', el)!=None ):    #devo splittare gli elementi con il comando split e lo spazio, dopodiche
			 					val=-1
			 									#cerco l'elemento da eliminare
			if(val==0):
				filtro.append(line)

		#print(filtro)
		#print("\n")

		#questa fase mi serve a prendere tutti i nodi e metterli nella lista nodi

		nodi=[]

		for line in filtro:
			for elems in line:
					elem=elems.split(" ")
			 		for el in elem:
			 			#print el
						if(re.search('nodo[0-9]+', el)!=None):    #devo splittare gli elementi con il comando split e lo spazio, dopodiche
			 				nodi.append(el)

			 		#print("new line")						

		#adesso ordiniamo i nodi e ridiamo l'indice giusto alla lista dei nodi
			
		nodi=set(nodi)
		nodi=sorted(nodi, key=num_node)
		#print nodi
		#rinominiamoli giusti nella lista filtro
		aux=[]
		aux2=[]
		for line in filtro:
			for elems in line:
					elem=elems.split(" ")
			 		for el in elem:
						if(re.search('nodo[0-9]+', el)!=None):    #devo splittare gli elementi con il comando split e lo spazio, dopodiche
			 				if(el in nodi):
			 					#aux.append(nodi.index(el)+1)
			 					val=nodi.index(el)+1
			 			else:
			 				val=el
			 			aux.append(val)



		#questo serve a splittare tutti gli elementi in una lista da un elemento per posizione

		aux2=[]
		for elem in aux:
			elem=str(elem)
			el=elem.split(",")
			if (len(el) > 1):
				for e in el:
					aux2.append(e)
			else:
				aux2.append(elem)


		#num_nodi lo utilizzo per dare 2 nuovi nodi ai nostri switch ed ai carichi
		num_nodi=len(nodi)+1
		aux3=[]
		for elem in aux2:
			if(re.match('Switch',elem)!=None or re.match('Load',elem)!=None or re.match('MG',elem)!=None or re.match('AUX',elem)!=None):
				val=elem+"_"+str(num_nodi)
				num_nodi=num_nodi+1
			else: val=elem
			aux3.append(val)

		#print aux3


		#a questo punto creiamo le linee che servono alla funzione in scala
		#Ricorda:
		#c.add_connection(Node(1),Node(2)) 		connessioni fra 2 nodi
		#c.add_switcher("swp1",Node(10),Node(11)) 			connessioni fra switch e due nodi
		#c.sw_map += ("swp1"->"sws1")						or esclusivo fr i "p" ed "s"
		#c.add_load("l2",Node(11))							carico connesso ad un nodo
		#c.add_generator("mg1",Node(24))					generatore connesso ad un nodo


		# ogni switch avra 2 nodi 

		#ad ogni switch e ad ogni carico bisogna associare due nuovi nodi
		#cominciamo con i collegamenti fra nodi 
		#print aux2

		#cominciamo con le connessioni fra 2 nodi
		out_file = open("sps_circuit.txt","w")

		#step 1: connessioni nodo-nodo
		out_file.write("[node-node]\n")

		for i in range(0,len(aux3)-1,2):
			nod_1=[]
			nod_2=[]
			if(re.match('[0-9]',aux3[i])==None):
				nod_1=aux3[i].split("_")
			else:
				nod_1.append(aux3[i])
			if(re.match('[0-9]',aux3[i+1])==None):
				nod_2=aux3[i+1].split("_")
			else:
				nod_2.append(aux3[i+1])

			out_file.write(nod_1[-1]+","+nod_2[-1]+"\n")



		#step 2: connessioni carico-nodo-pwr
		x=0
		out_file.write("[load-node-pwr]\n")
		for i in range(0,len(aux3)):
			if(re.match('Load',aux3[i])!=None):
				nod_L=aux3[i].split("_")
				for block in System.findall('Block'):   #adesso trovo tutti i valori dei carichi
					name=block.get("Name")
					name=name.replace(' ','')
					if(name==nod_L[0]):
						RR=block.find("InstanceData")
						RR = RR.findall('P')
						for R in RR:
							if(R.get("Name")=="R"):
								out_file.write(nod_L[0].lower()+","+nod_L[1]+","+R.text+"\n")
								x=x+1
		print ("numero load: "+str(x))

		#step 3: connessioni generatori-nodo-pwr
		x=0
		out_file.write("[gen-node-pwr]\n")
		for i in range(0,len(aux3)):
			if(re.match('AUX',aux3[i])!=None or re.match('M',aux3[i])!=None):
				nod_L=aux3[i].split("_")
				for block in System.findall('Block'):   #adesso trovo tutti i valori dei carichi
					name=block.get("Name")
					name=name.replace(' ','')
					if(name==nod_L[0]):
						RR=block.find("InstanceData")
						RR = RR.findall('P')
						for R in RR:
							if(R.get("Name")=="v0"):
								out_file.write(nod_L[0].lower()+","+nod_L[1]+","+R.text+"\n")
								x=x+1
		print ("numero gen: "+str(x))

		visitedS=[]
		out_file.write("[switch-node-node]\n")
		x=0
		#step 4: connessioni switch con i suoi nodi
		for i in range(0,len(aux3)):
			val=0
			if(re.match('Switch',aux3[i])!=None):
				nod_S1=aux3[i].split("_")
				for vis in visitedS:
					if (vis==nod_S1[0]):
						val=1
				if(val==0):
					visitedS.append(nod_S1[0])
					for j in range(i+1,len(aux3)):
						if(re.match('Switch',aux3[j])!=None):
							nod_S2=aux3[j].split("_")
							if(nod_S1[0]==nod_S2[0]):
								out_file.write(nod_S1[0].replace("-","").lower()+","+nod_S1[1]+","+nod_S2[1]+"\n")
								x=x+1
		print ("numero switch: "+str(x))

		#step 5: mutua esclusione switch sws swp
		SwitchS_P=[]
		num=0
		for i in range(0,len(aux3)):
			if(re.match('SwitchSWS',aux3[i])!=None):
				num=num+1
		num=num/2
		#num indica il numero di switch SWS da associare ad SWP
		out_file.write("[mutex]\n")
		for i in range(1,num+1):
			out_file.write("switchsws"+str(i)+","+"switchswp"+str(i)+"\n")
		#out_file.write(str(num)+"\n")
				
		out_file.close()

if __name__ == "__main__":
    main()




