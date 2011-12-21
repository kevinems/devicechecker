

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#include "com_antroid_check_CheckSum.h"

#define LOGI_D(...) __android_log_print(ANDROID_LOG_DEBUG, "ChecksumMsg", __VA_ARGS__)
#define LOGI_I(...) __android_log_print(ANDROID_LOG_INFO, "ChecksumMsg", __VA_ARGS__)
#define LOGI_W(...) __android_log_print(ANDROID_LOG_WARN, "ChecksumMsg", __VA_ARGS__)
#define LOGI_E(...) __android_log_print(ANDROID_LOG_ERROR, "ChecksumMsg", __VA_ARGS__)

#define MAX_READBUFFER      1024*1024
#define UINT16 	unsigned short
#define UINT32 	unsigned int
#define UINT 		unsigned int
#define INT8 		char
#define UINT8 	unsigned char
#define INT32		int
#define INT			int
#define BOOL 		char
#define true		1
#define false		0

#define NO_ERROR							0
#define ERR_NOLISTFILE				1
#define ERR_LISTFILEILLEGAL		2
#define ERR_MEMERR						3
#define ERR_READLISTFILEERR		4
#define ERR_RESFILEERR				5
#define ERR_TOTALSUM					6

#define	TextMaxSize 				512*2

#define FILELIST_C_PATH				"/config/__chksum.$$$"
#define FILELIST_D_PATH				"/data/M16/__chksum.$$$"

typedef struct tagCheckSumInfo
{
    UINT32          oldOffTime;
    BOOL            checkSuming;
    UINT8           textInfo[TextMaxSize];               //提示信息
    
    INT8            *showBuffer;
    INT8            *showTempBuffer;
    INT8            *listBuffer;
    INT8            *readBuffer;
		INT8            *fileName;
			
    UINT16          totalCheckSum;
    UINT16          getCheckSum;   

    UINT16          testRunTime;
    
    jclass classTmp;
		jmethodID getUTFFileName;
} CheckSumInfo;

BOOL CalcFunc(JNIEnv *env, char *pListFile, char *pResult,int type);
BOOL CheckSumGetPathAndValue(JNIEnv *env, INT8 **pListBuffer, UINT8 *currFilePath , char *pValueStr,int type);
BOOL CheckSumAllCode( char *pResult );
UINT16 CheckSumCalc(UINT8 *Buf, UINT32 Len);
BOOL CheckSumGetTotalValue(INT8 **pListBuffer, UINT8 *valueBuf);
CheckSumInfo g_CheckSumInfo;

char *jbyteArrayToChar(JNIEnv *env,jbyteArray data)
{
	return (char *)(*env)->GetByteArrayElements(env,data,NULL);
}

/*
 * Class:     com_antroid_check_CheckSum
 * Method:    setFileName
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_smit_com_factorytest_CheckSumActivity_setFileName
  (JNIEnv *env, jobject thiz, jbyteArray byte_data)
  {
  	char *pFilePath;
  	char file_pathp[256];
  	int len = 0;
  	
  	memset(file_pathp,0,256);
  	len = (*env)->GetArrayLength(env,byte_data);
  	pFilePath = (char *)jbyteArrayToChar(env,byte_data);   	
  	strncpy(file_pathp,pFilePath,len);
  	//LOGI_D("OPEN FILE:%s\n",pFilePath);
		memset(g_CheckSumInfo.fileName,0,256);
		strcpy(g_CheckSumInfo.fileName,file_pathp);
		
		(*env)->ReleaseByteArrayElements(env,byte_data,(char*)pFilePath,0);
  }

/*
 * Class:     com_antroid_checksum_CheckSum
 * Method:    CalculateCheckSumJni
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jbyteArray JNICALL Java_smit_com_factorytest_CheckSumActivity_CalculateJni
  (JNIEnv *env, jobject thiz)

  {
  	jstring jstr;
	BOOL bRet = false; 	
	char TotalResultSz[1024] = {0};
	char SubResultSz[256] = {0};
	jbyteArray byte_data;
	
	do
	{
		
		memset(SubResultSz, 0, 256);
		bRet = CalcFunc(env,FILELIST_C_PATH, SubResultSz,0) ;
		strcat(TotalResultSz, SubResultSz);
	
		if(bRet == false){
			break;	
		}
		
		
		memset(SubResultSz, 0, 256);
		
		bRet = CalcFunc(env, FILELIST_D_PATH, SubResultSz,1);
		strcat(TotalResultSz, SubResultSz);
		if(bRet == false){
			break;	
		}
		
		memset(SubResultSz, 0, 256);
		bRet = CheckSumAllCode(SubResultSz);
		strcat(TotalResultSz, SubResultSz);
		if(bRet == false){
			break;	
		}
		
		bRet = true;
		
	}while(0);
	
	byte_data = (*env)->NewByteArray(env,strlen(TotalResultSz));
	(*env)->SetByteArrayRegion(env,byte_data,0,strlen(TotalResultSz),(char *)TotalResultSz);
	return byte_data;
  }
  
BOOL CalcFunc(JNIEnv *env, char *pListFile, char *pResult,int type)
{	
		FILE	*fp = NULL;
    UINT16  sum = 0;
    UINT32  listLen = 0;
    INT8    *pListBuffer; 
    INT8    currFilePath[512];
    INT8    pSumStr[32];
    INT32   getFileResult;
    INT32		errType = -1;
    char		sz[128] = {0};
    char		szValue[32] = {0};
    BOOL 		bRet = false;
    
    memset(&g_CheckSumInfo, 0, sizeof(CheckSumInfo));
		
		do
		{
		    fp = fopen(pListFile, "rb");
		    LOGI_D("file path:%s -- and file fp:0x%x",pListFile,fp);
		    if ( fp == NULL )
		    {
		        errType = ERR_NOLISTFILE;
		        break;
		    }
		    fseek(fp, 0, SEEK_END);
		    listLen = ftell(fp);
		    if(listLen <= 0)
		    {
		    	errType = ERR_LISTFILEILLEGAL;
		    	break;	
		    }
		
		    g_CheckSumInfo.listBuffer = (UINT8*)malloc(listLen+1);
		    if ( g_CheckSumInfo.listBuffer == NULL )
		    {
		    	errType = ERR_MEMERR;
					break;
		    }    
		    memset(g_CheckSumInfo.listBuffer, 0, listLen+1);
		    fseek(fp, 0, 0);
		    if(fread(g_CheckSumInfo.listBuffer, 1, listLen, fp) != listLen)
		    {
		    	errType = ERR_READLISTFILEERR;
		    	break;
		    }
				fclose(fp);
		
		    pListBuffer = g_CheckSumInfo.listBuffer;
				g_CheckSumInfo.readBuffer = (char *)malloc(MAX_READBUFFER + 1);
				if(g_CheckSumInfo.readBuffer == NULL)
				{
					errType = ERR_MEMERR;
					break;
				}
				memset(g_CheckSumInfo.readBuffer,0,MAX_READBUFFER +1);
				
				g_CheckSumInfo.fileName = (char *)malloc(256);
				if(g_CheckSumInfo.fileName == NULL)
				{
					errType = ERR_MEMERR;
					break;
				}
				
				//g_CheckSumInfo.classTmp = (*env)->FindClass(env,"com/antroid/check/CheckSum");
				g_CheckSumInfo.classTmp = (*env)->FindClass(env,"smit/com/factorytest/CheckSumActivity");
				LOGI_D("currFilePath is %s\n", currFilePath);
		
        g_CheckSumInfo.getUTFFileName = (*env)->GetStaticMethodID(env
											,g_CheckSumInfo.classTmp,"getUTF8ByteArray","([BI)V");
				
		    while ( CheckSumGetPathAndValue(env, &pListBuffer, currFilePath, szValue ,type) )
		    {
		    		if(g_CheckSumInfo.fileName)
		    		{
		    			memset(currFilePath,0,256);
		    			if(g_CheckSumInfo.fileName[1] == ':')
	 						{
	   							strcpy(currFilePath,g_CheckSumInfo.fileName + 2);
	 						}
	 						else
	  					{
	 							 strcpy(currFilePath,g_CheckSumInfo.fileName);
	 						}
		    		}
		    		//LOGI_D("++++++++fileName:%s",currFilePath);
		        getFileResult = CheckSumGetFile( currFilePath, g_CheckSumInfo.readBuffer, MAX_READBUFFER, &sum);
		        if ( getFileResult >= 0 )
		        {
		        	memset(sz, 0, 128);
		        	sprintf(sz, "0x%x", sum);
		        	///LOGI_D("File Check sum:%s------right:%s",sz,szValue);
		        	if(my_stricmp(szValue, sz) == 0)
		        	{
								g_CheckSumInfo.totalCheckSum += sum;
							}
							else
							{
								errType = ERR_RESFILEERR;
		            goto END;
							}
		        }
		        else
		        {
		            errType = ERR_RESFILEERR;
		            goto END;
		        }		
		    }
		    
		    CheckSumGetTotalValue(&pListBuffer, szValue);
		    memset(sz, 0, 128);
      	sprintf(sz, "0x%x", g_CheckSumInfo.totalCheckSum);
      	if(my_stricmp(szValue, sz) != 0)
      	{
      		errType = ERR_TOTALSUM;
      		break;
      	}

			errType = NO_ERROR;
		}while(0);
		
END:
		switch(errType)
		{
			case ERR_NOLISTFILE:
				LOGI_E("No List File ! ! ! \n");
				sprintf(pResult, "No List File ! ! ! \n");
				break;
			case ERR_LISTFILEILLEGAL:
				LOGI_E("List File Illegal ! ! ! \n");
				sprintf(pResult, "List File Illegal ! ! ! \n");
				break;
			case ERR_MEMERR:
				LOGI_E("Memory Error ! ! ! \n");
				sprintf(pResult, "Memory Error ! ! ! \n");
				break;
			case ERR_READLISTFILEERR:
				LOGI_E("Read List File Error ! ! ! \n");
				sprintf(pResult, "Read List File Error ! ! ! \n");
				break;
			case ERR_RESFILEERR:
				LOGI_E("File Error ! ! ! \n %s", currFilePath);
				sprintf(pResult, "File Error ! ! ! \n %s", currFilePath);
				break;
			case ERR_TOTALSUM:
				LOGI_E("Total sum Error ! ! ! \n");
				sprintf(pResult, "Total sum Error ! ! ! \n");
				memset(sz, 0, 128);
				sprintf(sz, "Calculate Total sum is 0x%x , Right sum should be %s \n", g_CheckSumInfo.totalCheckSum, szValue);
				break;
				
			case NO_ERROR:
			default:
				memset(sz, 0, 128);
				LOGI_D("Check Files OK.\n");
				sprintf(pResult, "Check Files OK.\n");
				if(type == 0)
				{
					sprintf((char*)sz, (char*)"系统文件校验总值 = 0x%x\n\n", g_CheckSumInfo.totalCheckSum );
				}
				else
				{
					sprintf((char*)sz, (char*)"预置资料校验总值 = 0x%x\n\n", g_CheckSumInfo.totalCheckSum );
				}
				
				strcat( pResult, sz );
				bRet = true;
				break;
		}    

    if ( g_CheckSumInfo.listBuffer != NULL )
    {
        free(g_CheckSumInfo.listBuffer);
        g_CheckSumInfo.listBuffer = NULL;
    }
    if(g_CheckSumInfo.readBuffer)
    {
    		free(g_CheckSumInfo.readBuffer);
    		g_CheckSumInfo.readBuffer = NULL;
    }
    if(g_CheckSumInfo.fileName)
    {
    	free(g_CheckSumInfo.fileName);
    	g_CheckSumInfo.fileName = NULL;
    }
    LOGI_D("CalcFunc return %d", bRet);
    return bRet;
}


BOOL CheckSumAllCode( char *pResult )
{
	BOOL bRet = true;
	strcpy(pResult, "system code OK.");
	
	return bRet;
}
int CheckSumTranslateFilePath(char *currFilePath,int len)
{
	int i = 0;
	for(i = 0;i < len;i++)
	{	if(currFilePath[i] == '\\' )
		{
		currFilePath[i] = '/';
		}
	}
	return 1;
}
  //--------------------------------------------
//获得文件路径
//--------------------------------------------
BOOL CheckSumGetPathAndValue(JNIEnv *env, INT8 **pListBuffer, UINT8 *currFilePath , char *pValueStr,int type)
{
    INT8 *cLocat = NULL;
    INT8 *valStart = NULL, *valEnd = NULL, *p = NULL;
    UINT32 pathLen = 0;
    UINT32 valLen = 0;
    jstring	str = NULL;
    jboolean isCopy;
    char *DstPath = NULL;

    jbyteArray byte_data;
    char *pFileName;
    
		memset(currFilePath,0,512);
		memset(pValueStr,0,32);
    cLocat = strstr( *pListBuffer, "	【" );
    if ( cLocat == NULL )
    {
        return false;
    }
   // LOGI_D("CheckSumGetPathAndValue");
   
	  
    pathLen = cLocat - *pListBuffer; 
    strncat( currFilePath, *pListBuffer, pathLen );
	  
    CheckSumTranslateFilePath(currFilePath,pathLen);
    
	 
		if(g_CheckSumInfo.getUTFFileName == NULL)
		{
			LOGI_D("GET STATIC METHOD ERRO!");
		}
		byte_data = (*env)->NewByteArray(env,pathLen);
  	(*env)->SetByteArrayRegion(env,byte_data,0,pathLen,(char *)currFilePath);
		
    (*env)->CallStaticVoidMethod(env,g_CheckSumInfo.classTmp,g_CheckSumInfo.getUTFFileName,byte_data,type);
		//LOGI_D("------FILENAME:%s",g_CheckSumInfo.fileName);
		
		//get value string
		p = strstr(*pListBuffer, "【");
		valStart = p+2;
		valEnd = strstr(*pListBuffer, "】");
		valLen = valEnd - valStart;
		memcpy(pValueStr, valStart, valLen);

		//
    cLocat = strchr( *pListBuffer, '\n' );
    cLocat++;
    *pListBuffer = cLocat;
		(*env)->ReleaseByteArrayElements(env,byte_data,(char*)jbyteArrayToChar(env,byte_data),0);
    return true;
}

 
//--------------------------------------------
//计算当前文件的CheckSum
//--------------------------------------------
INT32 CheckSumGetFile(UINT8 *FileName, UINT8 *ReadBuf, UINT32 bufLen, UINT16 *Sum)
{
	INT 	ret 	= 1;
	UINT 	Len 	= 0;
	INT 	ReadLen = 0;
	UINT 	Count 	= 0;
	FILE 	*fp		= NULL;
	
	*Sum = 0;
	fp 	= fopen(FileName, "rb");
	if(NULL == fp)
	{
		LOGI_D("open file erro!");
		return -1;
	}
	fseek(fp, 0, 2);
	Len = ftell(fp);
	fseek(fp, 0, 0);
	//LOGI_D("get file check sum!");
	while(1)
	{
		if(Count >= Len)
		{ret = 1; break;}
			
		if(Count + bufLen <= Len)
			ReadLen = bufLen;
		else
			ReadLen = Len - Count;
		//LOGI_D("READ FILE BEGIN");
		ret = fread(ReadBuf, 1, ReadLen, fp);
		if(ret != ReadLen)
		{
			LOGI_D("READ ERRO!");
			ret = -2;
			break;
		}
		else
			(*Sum) += CheckSumCalc(ReadBuf, ReadLen);
		Count += bufLen;
	}
	
	fclose(fp);
	//LOGI_D("___+_+_RET :%d",ret);
	return ret;
}


//--------------------------------------------
//计算CheckSum
//--------------------------------------------
UINT16 CheckSumCalc(UINT8 *Buf, UINT32 Len)
{
	UINT16 Sum = 0;
	
	while(Len--)
	{
		Sum += *Buf++;
	}	
	return Sum;
}


BOOL CheckSumGetTotalValue(INT8 **pListBuffer, UINT8 *valueBuf)
{
	INT32 i;
	UINT8 szBuf[128] = {0};
	INT8 *cLocat = NULL;

    cLocat = strchr( *pListBuffer, '\n' );
    cLocat++;
    *pListBuffer = cLocat;

		strcpy(szBuf, "CHKSUM: ");
    cLocat = strstr( *pListBuffer, szBuf);
    if ( cLocat == NULL ){
		printf("Can't find the 'CHKSUM' symbol!\n");
        return false;
    }
	
	cLocat += strlen(szBuf);
	memcpy(valueBuf, cLocat, 7);
	for(i = 0; i < 7; i++)
	{
		if(valueBuf[i] == 0x20){
			valueBuf[i] = 0x00;
			break;
		}
	}

	return true;
}

//功能：比较字符串s1和s2，但不区分字母的大小写。
//
//说明：
//      当s1<s2时，返回值<0
//      当s1=s2时，返回值=0
//      当s1>s2时，返回值>0
int my_stricmp(const char *dst, const char *src)
{
   int ch1, ch2;

   do
   {
      if ( ((ch1 = (unsigned char)(*(dst++))) >= 'A') &&(ch1 <= 'Z') )
        ch1 += 0x20;

      if ( ((ch2 = (unsigned char)(*(src++))) >= 'A') &&(ch2 <= 'Z') )
        ch2 += 0x20;

   } while ( ch1 && (ch1 == ch2) );

   return(ch1 - ch2);
}


