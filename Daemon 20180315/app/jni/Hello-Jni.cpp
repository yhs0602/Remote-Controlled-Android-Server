/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <jni.h>


	//JNIEXPORT jstring JNICALL Java_com_kyunggi_medinology_MainActivity_stringFromJNI(JNIEnv* env, jobject thiz)
	//{
	//	return env->NewStringUTF("Hello from JNI !");
extern "C"
{
	int Hanja2Hangul(char *s);	/* input should be UTF-8 encoded stream */
	JNIEXPORT void JNICALL Java_com_kyunggi_worker_HanjaUtil_HanjaToHangeul(JNIEnv* env, jobject thiz,jbyteArray bytarr)
	{
		int len=env->GetArrayLength(bytarr);
		unsigned char *data= new unsigned char[len];
		jbyte *byte_buf;
        byte_buf = env->GetByteArrayElements(bytarr, NULL);
		for(int i=0;i<len;++i)
		{
			data[i]=(float)byte_buf[i];
		}
		env->ReleaseByteArrayElements(_symptoms, byte_buf, 0);
		Hanja2Hangul(data);
		
		//net= TwoLayerNet(72,HIDDEN_LAYER,/*NUM_Diseases*/31);
	}

	/*JNIEXPORT void JNICALL Java_com_kyunggi_medinology_MainActivity_initWeights(JNIEnv* env, jobject thiz/*jstring filename*/)
	{
		//const char *fname = env->GetStringUTFChars( filename, NULL);//Java String to C Style string
		//LoadWeights("/sdcard/weight.txt");
 		//env->ReleaseStringUTFChars( filename, );
		//logger<<"weight successfully loaded"<<endl;
	}*/
	#include "HanjaHangulMap.h"

int Hanja2Hangul(char *s)	/* input should be UTF-8 encoded stream */
	{
		unsigned char c;
		unsigned short unicode = 0x00000000;

		int i, j, len;

		len = strlen(s);
		for (i = 0; i < len;)
		{
			c = s[i] & 0xe0;
			if (c < 0x80)
			{
				i++;
				continue; /* no need to process chars in this area */
			}
			else if (c < 0xe0)
			{
				i += 2;
				continue; /* no need to process chars in this area */
			}
			else if (c < 0xf0)
			{
				unicode = (unsignedshort) s[i] & 0x0f;
				i++;
				unicode = unicode << 6;
				unicode = unicode | ((unsignedshort) s[i] & 0x3f);
				i++;
				unicode = unicode << 6;
				unicode = unicode | ((unsignedshort) s[i] & 0x3f);
				i++;
			} /* from UTF-8 to UCS-2 */

			if (HjHgMap[unicode] != unicode)
			{ /* different ? hanja: non-CJK */
				unsigned char byte[4];
				/* full checking
				 int nbytes;
				 unicode = HjHgMap[unicode];
				 if (unicode < 0x80) {
				 nbytes = 1;
				 byte[0] = unicode;
				 } else if (unicode < 0x800) {
				 nbytes = 2;
				 byte[1] = (unicode & 0x3f) | 0x80;
				 byte[0] = ((unicode << 2) & 0xcf00 | 0xc000) >> 8;
				 } else {
				 nbytes = 3;
				 byte[2] = (unicode & 0x3f) | 0x80;
				 byte[1] = ((unicode << 2) & 0x3f00 | 0x8000) >> 8;
				 byte[0] = ((unicode << 4) & 0x3f0000 | 0xe00000) >> 16;
				 }
				 */
				/* entered here, we guarantee unicode is greater than 0x0800 */
				unicode = HjHgMap[unicode];
				byte[2] = (unicode & 0x3f) | 0x80;
				byte[1] = ((unicode << 2) & 0x3f00 | 0x8000) >> 8;
				byte[0] = ((unicode << 4) & 0x3f0000 | 0xe00000) >> 16;
				for (j = 0; j < 3; j++)
				{
					s[i - 3 + j] = byte[j];
				}
				continue;
			}
		}
		return 0;
	}

}
	
