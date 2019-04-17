package org.civis.blockchain.docstampr.api.document

import com.google.common.io.ByteStreams
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import org.civis.blockchain.docstampr.api.rest.crypto.AESCipher
import org.civis.blockchain.docstampr.api.rest.document.GitException
import org.civis.blockchain.ssm.client.Utils.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig.Host
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.Transport
import org.eclipse.jgit.util.FS
import java.io.*
import javax.crypto.SecretKey


class GitBaseCommand(docstamprGitRepo: String, keyGitRepo: String) {

    private val repo : Git = getRepo(docstamprGitRepo)

    private val sshSessionFactory: SshSessionFactory = jschConfigSessionFactory(keyGitRepo)

    fun workTree(): File {
        return repo.repository.workTree
    }

    fun listBranch() {

        repo.use { git ->
            var call = git.branchList().call()
            for (ref in call) {
                println("Branch: " + ref + " " + ref.name + " " + ref.objectId.name)
            }

            println("Now including remote branches:")
            call = git.branchList().setListMode(ListMode.ALL).call()
            for (ref in call) {
                println("Branch: " + ref + " " + ref.name + " " + ref.objectId.name)
            }
        }
    }

    fun existBranch(name: String): Boolean {
        for (ref in repo.branchList().call()) {
            if (ref.name.equals("refs/heads/"+name)) {
                return true
            }
        }
        return false
    }

    fun createBranch(name: String) {
        repo.use { git ->
            git.branchCreate()
                    .setName(name)
                    .call()
        }
    }

    fun checkoutBranch(name: String) {
        val create = !existBranch(name)
        repo.use { git ->
            git.checkout()
                    .setCreateBranch(create)
                    .setName(name)
                    .call()
        }
    }

    fun createEmptyFile(fileName: String): OutputStream {
        val fileToCreate = File(repo.repository.workTree.absolutePath, fileName)
        if(fileToCreate.exists()) {
            throw GitException("File "+fileName+" already exists");
        }
        fileToCreate.createNewFile();
        return FileOutputStream(fileToCreate)
    }

    fun createFile(filename: String, data: InputStream) {
        createEmptyFile(filename).use { file ->
            ByteStreams.copy(data,  file)
        }
    }

    fun createFile(filename: String, data: FileInputStream, encryptKey: SecretKey) {
        createEmptyFile(filename).use { file ->
            AESCipher().encrypt(encryptKey, data, file)
        }
    }

    fun commitFile(filename: String) {
        repo.add().addFilepattern(filename).call();
        repo.commit().setMessage("Add "+filename).call();
    }

    fun pushBranch() {
        repo.push()
                .setRemote("origin")
                .setTransportConfigCallback(object : TransportConfigCallback {
                    override fun configure(transport: Transport) {
                        val sshTransport = transport as SshTransport
                        sshTransport.sshSessionFactory = sshSessionFactory
                    }
                })
                .call()
    }

    private fun getRepo(docstamprGitRepo: String): Git {
        val url = FileUtils.getUrl(docstamprGitRepo)
        return Git.open(File(url.file))
    }

    private fun jschConfigSessionFactory(keyGitRepo: String): JschConfigSessionFactory {
        return object : JschConfigSessionFactory() {
            override fun configure(hc: Host?, session: Session?) {
            }

            @Throws(JSchException::class)
            override fun createDefaultJSch(fs: FS): JSch {
                val defaultJSch = super.createDefaultJSch(fs)
                val url = FileUtils.getUrl(keyGitRepo)
                defaultJSch.addIdentity(url.file)
                return defaultJSch
            }
        }
    }

}