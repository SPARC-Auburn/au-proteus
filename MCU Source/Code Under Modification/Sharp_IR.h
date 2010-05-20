// filename ******** Sharp_IR.h **************
// A Sharp IR driver for the Proteus Robot
// For use with Proteus IR Switch V3+ circuit board

/*
 ** Pin connections **
    
    PAD00 -> Front Left Sensor
    PAD01 -> Front Center Sensor
    PAD02 -> Front Right Sensor
    PAD03 -> Rear Left Sensor
    PAD04 -> Rear Center Sensor
    PAD05 -> Rear Right Sensor
    

 ** Feature Usage **
   
   6 pins on ADC0
*/

#ifndef _Sharp_IR_H
#define _Sharp_IR_H 1

#define ADC_IR_FL 0x83 
#define ADC_IR_FC 0x84
#define ADC_IR_FR 0x85
#define ADC_IR_RL 0x80
#define ADC_IR_RC 0x81
#define ADC_IR_RR 0x82 
#define ADC_IR_E0 0x120
#define ADC_IR_E1 0x121
#define ADC_IR_E2 0x122
#define ADC_IR_E3 0x123
#define ADC_IR_E4 0x124
#define ADC_IR_E5 0x125

//all return units of mm
unsigned short IR_getFL(void); //front left
unsigned short IR_getFC(void); //front center
unsigned short IR_getFR(void); //front right
unsigned short IR_getRR(void); //rear left
unsigned short IR_getRC(void); //rear center
unsigned short IR_getRL(void); //rear right
unsigned short IR_getE0(void); //extra 0
unsigned short IR_getE1(void); //extra 1
unsigned short IR_getE2(void); //extra 2
unsigned short IR_getE3(void); //extra 3
unsigned short IR_getE4(void); //extra 4
unsigned short IR_getE5(void); //extra 5

#endif /* _Sharp_IR_H */