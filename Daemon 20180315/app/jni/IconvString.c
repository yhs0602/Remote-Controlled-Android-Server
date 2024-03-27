/* by Jinsuk Kim, http://www.jinsuk.pe.kr */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iconv.h>
#include <errno.h>

int IconvString (char *from, char *to, const char *src, char *dst)
{
    size_t srclen;
    size_t dstlen;
    size_t inleftsize, outleftsize;
    size_t res;				/* report of iconv */
    iconv_t cd = (iconv_t)-1;

    char *inptr;
    char *outptr;

    /* open iconv */
    cd = iconv_open(to, from);
    if (cd==(iconv_t)(-1)) {
	return (int)cd;
    }

    if (!strcasecmp(from, "UCS-2")) inleftsize=2;
    else {
	srclen = (size_t)strlen(src);
	inleftsize = srclen;
    }
    outleftsize = inleftsize*4;
    dstlen = outleftsize;
    inptr = (char*)src;
    outptr = dst;

    while(1) {
	res = iconv(cd,(const char **)&inptr,&inleftsize,&outptr,&outleftsize);
	if (res == (size_t)(-1)) {
	    if (errno == EILSEQ) { /* not defined char in the table ? */
		fprintf(stderr, "iconv_str: can't convert[%s]\n", src);
		/* for 2-byte code incompleteness */
		inptr++;
		inleftsize--;
	    }
	    else if (errno == EINVAL) { /* incomplete char, need readin more codes */
		fprintf(stderr, "iconv_str: incomplete char or shift sequence\n");
		if (inleftsize <= 2) {
		    *outptr = '?';
		    outleftsize--;
		    break;
		}
	    }
	    *outptr='?';
	    outptr++;
	    outleftsize--;
	    inptr++;
	    inleftsize--;
	}
	else break;
    }
    dst[dstlen-outleftsize] = '\0';
    /* close iconv */
    iconv_close(cd);
    return(dstlen-outleftsize);
}

