/* by Jinsuk Kim, http://www.jinsuk.pe.kr */

#include <stdio.h>
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

