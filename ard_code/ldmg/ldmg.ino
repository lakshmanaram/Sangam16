// the setup function runs once when you press reset or power the board
//
/*
 * 2,3 - left pulley 2 - high - up
 * 12,13 - right pulley 12 - high - up
 * 6,7 - grip front/back 6 - high - front
 * 34,36 - 34 - high - close gripper 
 * 8,9,10,11 base (8,10 - high - base_front
 * 36 - enable for base
 * 8,9 - 200 rpm motor
 */
char val,rack;
int ledpin = 2;  // LED connected to pin 2 (on-board LED)
int i=0,j=0,rf;
int bluetooth = 0;
String rfid[6] = {"1321231","12412414","1241242141","12412412","1241241","124124124"};
int book_read;
void move_up(float time_in_seconds)
{

	analogWrite(2, 130);   // turn the LED on (HIGH is the voltage level)
	digitalWrite(3, LOW);   // back
    analogWrite(12, 200);   // turn the LED on (HIGH is the voltage level)
    digitalWrite(13, LOW); 
    delay((int)(time_in_seconds*1000));
  	digitalWrite(3, LOW);
  	digitalWrite(2, LOW);
  	digitalWrite(12, LOW);
  	digitalWrite(13, LOW);
}
void move_down(float time_in_seconds)
{
  analogWrite(3, 130);   // turn the LED on (HIGH is the voltage level)
  digitalWrite(2, LOW);   // back
  analogWrite(13, 130);   // turn the LED on (HIGH is the voltage level)
  digitalWrite(12, LOW);   // front
  delay((int)(time_in_seconds*1000));
  digitalWrite(3,LOW);
  digitalWrite(2,LOW);
  digitalWrite(13,LOW);
  digitalWrite(12,LOW);
  
}

void grip_front(float time_in_seconds)
{
	analogWrite(6, 100);
	digitalWrite(7,LOW);
	delay((int)(time_in_seconds*1000));
	digitalWrite(6,LOW);
	digitalWrite(7,LOW);
}

void grip_back(float time_in_seconds)
{
	analogWrite(7, 100);
	digitalWrite(6,LOW);
	delay((int)(time_in_seconds*1000));
	digitalWrite(6,LOW);
	digitalWrite(7,LOW);
}


void grip_hold(int x,float time_in_seconds)
{
	if(x==1)
	{
		digitalWrite(34,HIGH);
		digitalWrite(36,LOW);
		delay((int)(time_in_seconds*1000));
		digitalWrite(34,LOW);
		digitalWrite(36,LOW);
	}
	else if(x==2)
	{
		digitalWrite(36,HIGH);
		digitalWrite(34,LOW);
		delay((int)(time_in_seconds*1000));
		digitalWrite(34,LOW);
		digitalWrite(36,LOW);


	}

}



void base_front(float time_in_seconds)
{
	analogWrite(8,200);
	digitalWrite(9,LOW);
	analogWrite(10,250);
	digitalWrite(11,LOW);
	delay((int)(time_in_seconds*1000));
	digitalWrite(10,LOW);
	digitalWrite(11,LOW);
	digitalWrite(8,LOW);
	digitalWrite(9,LOW);
}
void base_back(float time_in_seconds)
{
	analogWrite(9,200);
	digitalWrite(8,LOW);
	analogWrite(11,250);
	digitalWrite(10,LOW);
	delay((int)(time_in_seconds*1000));
	digitalWrite(10,LOW);
	digitalWrite(11,LOW);
	digitalWrite(8,LOW);
	digitalWrite(9,LOW);

}


void setup()
{
	pinMode(12, OUTPUT);
	pinMode(13, OUTPUT);       //pulley1
	pinMode(3, OUTPUT); 
	pinMode(2, OUTPUT);        //pulley2
	pinMode(6,OUTPUT);
	pinMode(7,OUTPUT);           //frontback
	pinMode(10,OUTPUT);
	pinMode(11,OUTPUT);
	pinMode(36,OUTPUT);
	pinMode(34,OUTPUT);   //gripper
	pinMode(8,OUTPUT); //base1
	pinMode(9,OUTPUT);
	Serial1.begin(9600);							// bluetooth
	Serial.begin(9600);	
}

void base_front_rfid(int book){
	String bookstring = rfid[book_read - 1];
	base_front(2/*secs*/);
	if(Serial.readString().startswith(bookstring)||book == 1)
		return;
	base_front(1);
	if(Serial.readString().startswith(bookstring)||book == 2)
		return;
	base_front(1);
	if(Serial.readString().startswith(bookstring)||book == 3)
		return;
	Serial1.println("book not read error#");
}

void pick(){
	grip_front(3);
	Serial1.println("grip front#");
	grip_hold(1,4);
	Serial1.println("grip held#");
	grip_back(4);
	Serial1.println("grip back#");
}

void reach_back(int book){
	switch(book){
		case 1:
		base_back(2);
		break;
		case 2:
		base_back(3);
		break;
		case 3:
		base_back(4);
		break;
		default:
		Serial1.println("reach_back error#");
	}
}

void loop() {
	if(bluetooth == 0){
		if(Serial1.available()){
			bluetooth = 1;
			book_read = Serial1.read() - int('0');
			Serial1.println("Read book number & rfid#");
		}
	}else{
		switch(book_read){
			case 1:
			case 2:
			case 3:
			base_front_rfid(book_read);
			Serial1.println("rack 1#");
			break;
			case 4:
			case 5:
			case 6:
			move_up(12/*secs*/);
			base_front_rfid(book_read);
			Serial1.println("rack 2#");
			break;
			default:
			Serial1.println("racck error#");
		}
		Serial1.println("forward base movement done#");
		pick();
		Serial1.println("book picked#");
		switch(book_read){
			case 1:
			case 2:
			case 3:
			reach_back(book_read);
			Serial1.println("back base rack 1#");
			break;
			case 4:
			case 5:
			case 6:
			reach_back(book_read - 3);
			Serial1.println("back base rack 2#");
			break;
			default:
			Serial1.println("error back base#");
		}
		Serial1.println("Got the book!");
	}
}