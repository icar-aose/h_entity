ACTORS:
    ActuatorEnactor:
            dove: raspberry actuator
            ruolo: quando il capitano seleziona la soluzione, lui la esegue attraverso l'artefatto "SwitcherArtifact"
            entità con cui comunica: SwitcherArtifact, ReconfigurationEnactor

    CircuitMonitor:
             dove: locale
             ruolo: comunica con SensorArrayMonitor_1 e SensorArrayMonitor_2 per monitorare i valori delle correnti
                    comunica con SwitcherMonitor per conoscere lo stato dei relè
                    gestisce la DashBoard FaultAmpGui (in rpi_ina219)
                    comunica con la head per inviare i fault
             entità con cui comunica: SensorArrayMonitor_1, SensorArrayMonitor_2, SwitcherMonitor, FaultAmpGui, head

    FaultEnactor:
            dove: raspberry fault
            ruolo: tramite la head riceve i fault da eseguire sui relè dei fault, attraverso l'artefatto "faultArtifact"
            entità con cui comunica: head, faultArtifact

    MissionManager:
            dove: locale
            ruolo: gestisce la missione
            entità con cui comunica: head, entità che richiedono la missione

    PMRActor:
            dove: locale
            ruolo: trova le soluzioni e le invia all'SPSPlanGenerator
            entità con cui comunica: SPSPlanGenerator

    ReconfigurationEnactor:
            dove: locale
            ruolo: comunica con head per inviare la soluzione ricevuta all'ActuatorEnactor
            entità con cui comunica: ActuatorEnactor, head

    SensorArrayMonitor_1:
            dove: raspberry sensor
            ruolo: monitora 4 amperometri tramite gli attori SensorMonitor inviando i dati al CircuitMonitor
            entità con cui comunica: CircuitMonitor, SensorMonitor

    SensorArrayMonitor_2:
            dove: raspberry fault
            ruolo: monitora 2 amperometri tramite gli attori SensorMonitor inviando i dati al CircuitMonitor
            entità con cui comunica: CircuitMonitor, SensorMonitor

    SensorMonitor:
            dove: raspberry fault o sensor
            ruolo: controlla un amperometro tramite libreria rpi_ina219
            entità con cui comunica: SensorArrayMonitor_1 o _2

    SPSPlanGenerator:
            dove: locale
            ruolo: comunica con la head per inviargli le soluzioni trovate tramite PMRActor
            entità con cui comunica: head, PMRActor

    SPSPlanValidatorRem:
            dove: locale
            ruolo: comunica con la head per inviargli le soluzioni VALIDATE e NON VALIDATE tramite MatValidator
            entità con cui comunica: MatValidator, head

    SwitcherMonitor:
            dove: raspberry actuator
            ruolo: comunica con CircuitMonitor inviandogli lo stato dei relè (li legge lui direttamente)
            entità con cui comunica: CircuitMonitor

    MatValidator:
            dove: locale, ovunque ci sia Matlab installato (2018)
            ruolo: testa le soluzioni e le valida inviando al SPSPlanValidatorRem i risultati
            entità con cui comunica: SPSPlanValidatorRem

AGENT:
    head:
        dove: locale
        ruolo: controlla le varie comunicazioni
               gestisce le due GUI soluzioni validate e non validate ---> SolutionsGUI(CaptainInterface)


