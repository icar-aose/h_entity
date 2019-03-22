package org.icar.h.sps_management.worker

import org.icar.fol.{AssumptionSet, Entail}
import org.icar.musa.context.StateOfWorld
import org.icar.musa.pmr._
import org.icar.musa.scenarios.sps.{Circuit, Mission}

class SPSQualityAsset(circuit : Circuit, mission : Mission, assumptions: AssumptionSet) extends QualityAsset {
  val entail: Entail.type = Entail

  override def evaluate_node(w: StateOfWorld, goal_sat: Float): Float = evaluate_state(w).get

  override def evaluate_state(w: StateOfWorld): Option[Float] = {
    val cond_map = circuit.cond_map
    val res_map : Map[String, Boolean] = entail.condition_map(w,assumptions,cond_map)

    var supplied_pow : Float = 0
    for (g <- circuit.generators if res_map(g.id)) supplied_pow += mission.gen_pow(g.id)

    var residue_pow : Float = supplied_pow
    var absorbed_pow : Float = 0
    var not_enough_pow : Float = 0
    var digits : String = ""

    for (l_name <- mission.vitals) {

      if (res_map(l_name)) {
        absorbed_pow += mission.vital_pow
        if (residue_pow>mission.vital_pow) {
          digits += "1"
          residue_pow -= mission.vital_pow
        } else {
          digits += "0"
          not_enough_pow += mission.vital_pow
        }
      } else digits += "0"

    }

    for (l_name <- mission.semivitals) {

      if (res_map(l_name)) {
        absorbed_pow += mission.semivital_pow
        if (residue_pow>mission.semivital_pow) {
          digits += "1"
          residue_pow -= mission.semivital_pow
        } else {
          digits += "0"
          not_enough_pow += mission.semivital_pow
        }
      } else digits += "0"

    }

    for (l_name <- mission.nonvitals) {

      if (res_map(l_name)) {
        absorbed_pow += mission.nonvital_pow
        if (residue_pow>mission.nonvital_pow) {
          digits += "1"
          residue_pow -= mission.nonvital_pow
        } else {
          digits += "0"
          not_enough_pow += mission.nonvital_pow
        }
      } else digits += "0"

    }

    //println(digits)
    Some(Integer.parseInt(digits, 2)-not_enough_pow-residue_pow)
  }

  override def max_score: Float = math.pow(2, circuit.loads.length).toFloat

  override def pretty_string(w: StateOfWorld): String = {
    val cond_map = circuit.cond_map
    val res_map : Map[String, Boolean] = entail.condition_map(w,assumptions,cond_map)
    var digits : String = "["
    for (g <- circuit.generators)
      if (res_map(g.id)) digits+="1" else digits+="0"
    digits += " | "
    for (vital <- mission.vitals)
      if (res_map(vital)) digits+="1" else digits+="0"
    digits += " "
    for (semivital <- mission.semivitals)
      if (res_map(semivital)) digits+="1" else digits+="0"
    digits += " "
    for (nonvital <- mission.nonvitals)
      if (res_map(nonvital)) digits+="1" else digits+="0"

    //        for (l <- circuit.loads)
    //          if (res_map(l.id)) digits+="1" else digits+="0"
    digits += "]"
    digits
  }

  override def pretty_string(node: WTSStateNode): String = {
    "n("+pretty_string(node.w)+","+node.qos+")"
  }

  override def pretty_string(exp: WTSExpansion): String = {
    exp match {
      case s : SimpleWTSExpansion =>
        "x("+pretty_string(s.start.w)+"->"+pretty_string(s.end.w)+","+s.order+","+s.cap.name+")"
      case m : MultiWTSExpansion =>
        m.toString
    }
  }

}

