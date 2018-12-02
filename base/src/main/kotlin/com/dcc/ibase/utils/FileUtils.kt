package com.dcc.ibase.utils

import android.text.TextUtils
import java.io.*
import java.util.*

/**
 * 定禅天 净琉璃
 * 2018-11-21 21:23:42 Wednesday
 * 描述：文件工具
 */
object FileUtils {
    /**
     * 创建文件
     * @param path 文件的绝对路径
     * @return
     */
    fun createFile(path: String): Boolean {
        return !TextUtils.isEmpty(path) && createFile(File(path))
    }

    /**
     * 创建文件
     * @param file
     * @return 创建成功返回true
     */
    private fun createFile(file: File?): Boolean {
        if (file == null || !makeDirs(getFolderName(file.absolutePath)))
            return false
        if (!file.exists())
            return try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }

        return false
    }

    /**
     * 获得所在目录名称
     * @param filePath 文件的绝对路径
     * @return 如果路径为空或空串，返回路径名；不为空时，如果为根目录，返回"";
     * 如果不是根目录，返回所在目录名称，格式如：C:/Windows/Boot
     */
    private fun getFolderName(filePath: String): String? {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val filePosi = filePath.lastIndexOf(File.separator)
        return if (filePosi == -1) "" else filePath.substring(0, filePosi)
    }

    /**
     * 创建目录（可以是多个）
     * @param filePath 目录路径
     * @return  如果路径为空时，返回false；如果目录创建成功，则返回true，否则返回false
     */
    private fun makeDirs(filePath: String?): Boolean {
        if (TextUtils.isEmpty(filePath)) {
            return false
        }
        val folder = File(filePath!!)
        return folder.exists() && folder.isDirectory || folder.mkdirs()
    }

    /**
     * 获取某个目录下的文件名列表
     * @param path 目录
     * @return 某个目录下的所有文件名
     */
    fun getFileNameList(path: String): List<String> {
        if (TextUtils.isEmpty(path))
            return emptyList()
        val dir = File(path)
        val files = dir.listFiles() ?: return emptyList()
        val conList = ArrayList<String>()
        for (file in files) {
            if (file.isFile)
                conList.add(file.name)
        }
        return conList
    }

    /**
     * 复制文件
     * @param sourceFilePath 源文件目录（要复制的文件目录）
     * @param destFilePath 目标文件目录（复制后的文件目录）
     * @return 复制文件成功返回true，否则返回false
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyFile(sourceFilePath: String, destFilePath: String): Boolean {
        val inputStream = FileInputStream(sourceFilePath)
        return writeFile(destFilePath, inputStream)
    }

    /**
     * 读取文件的内容<br>
     * 默认utf-8编码
     * @param filePath 文件路径
     * @return 字符串
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readFile(filePath: String): String? {
        return readFile(filePath, "utf-8")
    }

    /**
     * 读取文件的内容
     * @param filePath 文件目录
     * @param charsetName 字符编码
     * @return String字符串
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readFile(filePath: String, charsetName: String): String? {
        var tempCharsetName = charsetName
        if (TextUtils.isEmpty(filePath))
            return null
        if (TextUtils.isEmpty(tempCharsetName))
            tempCharsetName = "utf-8"
        val file = File(filePath)
        val fileContent = StringBuilder("")
        if (!file.isFile)
            return null
        var reader: BufferedReader? = null
        try {
            val `is` = InputStreamReader(FileInputStream(
                    file), tempCharsetName)
            reader = BufferedReader(`is`)
            var line: String? = reader.readLine()
            while (line != null) {
                if (fileContent.toString() != "") {
                    fileContent.append("\r\n")
                }
                fileContent.append(line)
                line = reader.readLine()
            }
            return fileContent.toString()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * 向文件中写入数据<br></br>
     * 默认在文件开始处重新写入数据
     * @param filePath 文件目录
     * @param stream 字节输入流
     * @return 写入成功返回true，否则返回false
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeFile(filePath: String, stream: InputStream): Boolean {
        return writeFile(filePath, stream, false)
    }

    /**
     * 向文件中写入数据
     * @param filePath 文件目录
     * @param stream 字节输入流
     * @param append 如果为 true，则将数据写入文件末尾处；
     * 为false时，清空原来的数据，从头开始写
     * @return 写入成功返回true，否则返回false
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeFile(filePath: String, stream: InputStream?, append: Boolean): Boolean {
        if (TextUtils.isEmpty(filePath))
            throw NullPointerException("filePath is Empty")
        if (stream == null)
            throw NullPointerException("InputStream is null")
        return writeFile(File(filePath), stream,
                append)
    }

    /**
     * 向文件中写入数据
     * @param file 指定文件
     * @param stream 字节输入流
     * @param append 为true时，在文件开始处重新写入数据；
     * 为false时，清空原来的数据，从头开始写
     * @return 写入成功返回true，否则返回false
     * @throws IOException
     */
    @Throws(IOException::class)
    fun writeFile(file: File?, stream: InputStream,
                  append: Boolean): Boolean {
        if (file == null)
            throw NullPointerException("file = null")
        var out: OutputStream? = null
        try {
            createFile(file.absolutePath)
            out = FileOutputStream(file, append)
            val data = ByteArray(1024)
            var length: Int = stream.read(data)
            while (length != -1) {
                out.write(data, 0, length)
                length = stream.read(data)
            }
            out.flush()
            return true
        } finally {
            if (out != null) {
                try {
                    out.close()
                    stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * 向文件中写入数据
     * @param filePath 文件目录
     * @param content 要写入的内容
     * @param append 如果为 true，则将数据写入文件末尾处，而不是写入文件开始处
     * @return 写入成功返回true， 写入失败返回false
     * @throws IOException
     */
    @Throws(IOException::class)
    fun writeFile(filePath: String, content: String,
                  append: Boolean): Boolean {
        if (TextUtils.isEmpty(filePath))
            return false
        if (TextUtils.isEmpty(content))
            return false
        var fileWriter: FileWriter? = null
        try {
            createFile(filePath)
            val tempFile = File(filePath)
            if (!tempFile.exists()) {
                if (!tempFile.mkdirs()) {
                    return false
                }
            }
            fileWriter = FileWriter(filePath, append)
            fileWriter.write(content)
            fileWriter.flush()
            return true
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * 删除指定文件或指定目录内的所有文件
     * @param path 文件或目录的绝对路径
     * @return 路径为空或空白字符串，返回true；文件不存在，返回true；文件删除返回true；
     * 文件删除异常返回false
     */
    fun deleteFile(path: String): Boolean {
        return TextUtils.isEmpty(path) || deleteFile(File(path))
    }

    /**
     * 删除指定文件或指定目录内的所有文件
     * @param file
     * @return 路径为空或空白字符串，返回true；文件不存在，返回true；文件删除返回true；
     * 文件删除异常返回false
     */
    fun deleteFile(file: File?): Boolean {
        if (file == null)
            throw NullPointerException("file is null")
        if (!file.exists()) {
            return true
        }
        if (file.isFile) {
            return file.delete()
        }
        if (!file.isDirectory) {
            return false
        }

        val files = file.listFiles() ?: return true
        for (f in files) {
            if (f.isFile) {
                f.delete()
            } else if (f.isDirectory) {
                deleteFile(f.absolutePath)
            }
        }
        return file.delete()
    }

    /**
     * 删除目录
     * @param dirPath 目录路径
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteDir(dirPath: String): Boolean {
        return deleteDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录
     * @param dir 目录
     * @return `true`: 删除成功<br></br>`false`: 删除失败
     */
    fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        if (!dir.exists()) return true
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * 根据文件路径获取文件
     * @param filePath The logFileRelativePath of file.
     * @return 文件
     */
    private fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }
}