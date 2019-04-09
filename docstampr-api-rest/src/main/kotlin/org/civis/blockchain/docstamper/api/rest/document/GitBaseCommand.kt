package org.civis.blockchain.docstamper.api.document

import com.google.common.io.ByteStreams
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class GitBaseCommand(docstamperGitRepo: String) {

    private val repo = getRepo(docstamperGitRepo)

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

    fun commitFile(file: String, data: InputStream) {
        val myFile = File(repo.repository.workTree.absolutePath, file)
        ByteStreams.copy(data,  FileOutputStream(myFile))
        repo.add().addFilepattern(file).call();
        repo.commit().setMessage("Add "+file).call();
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

    var sshSessionFactory: SshSessionFactory = object : JschConfigSessionFactory() {
        override fun configure(hc: Host?, session: Session?) {
        }

        @Throws(JSchException::class)
        override fun createDefaultJSch(fs: FS): JSch {
            val defaultJSch = super.createDefaultJSch(fs)
            defaultJSch.addIdentity("/Users/apo/dev/smartb/git/docstampr-api/infra/id_rsa.github")
            return defaultJSch
        }

    }

    private fun getRepo(docstamperGitRepo: String): Git {
        val url = FileUtils.getUrl(docstamperGitRepo)

        return Git.open(File(url.file))
    }

}