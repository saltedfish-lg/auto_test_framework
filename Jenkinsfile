pipeline {
  agent any

  tools {
    maven 'Maven3.9.9'
    jdk 'JDK17'
  }

  environment {
    RECIPIENTS = '1325707506@qq.com'
    NOTIFY_CMD = 'java -cp target\\classes com.baidu.notification.SendNotificationMain'
  }

  stages {
    stage('Checkout') {
      steps {
        echo "🔍 当前状态：${currentBuild.currentResult}"
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        echo '🔧 编译项目并运行 Web 自动化测试 + 接口测试'
        echo "🔍 当前状态：${currentBuild.currentResult}"
        catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
          bat '''
            chcp 65001 > nul
            mvn clean test -DsuiteXmlFile=testng.xml
          '''
        }
      }
    }

    // ➕ 新增 Schema 校验日志阶段
    stage('Schema Validation Summary') {
      steps {
        echo '🧪 输出 Schema 校验结果摘要'
        script {
          def schemaLog = 'target/schema-validation.log'
          if (fileExists(schemaLog)) {
            def content = readFile(schemaLog)
            echo '📄 Schema 校验日志内容:'
            echo content

            // ➕ 添加为 Allure 附件
            writeFile file: 'target/allure-results/schema-summary.txt', text: content
            allure([
              includeProperties: false,
              results: [[path: 'target/allure-results']],
              reportBuildPolicy: 'ALWAYS'
            ])
          } else {
            echo '⚠️ 未发现 Schema 校验日志，可能未执行或无记录'
          }
        }
      }
    }

    stage('Generate Allure Report') {
      steps {
        echo '📊 生成 Allure 报告'
        echo "🔍 当前状态：${currentBuild.currentResult}"
        script {
          def allureResultExist = fileExists('target/allure-results') &&
                                   bat(script: 'dir target\\allure-results\\* >nul 2>&1', returnStatus: true) == 0

          if (allureResultExist) {
            catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
              allure([
                results: [[path: 'target/allure-results']],
                reportBuildPolicy: 'ALWAYS'
              ])
            }

            if (currentBuild.result == 'UNSTABLE') {
              echo '⚠️ Allure 执行后标记为 UNSTABLE，尝试修正为 SUCCESS'
              script { currentBuild.result = 'SUCCESS' }
            }
          } else {
            echo 'ℹ️ 未发现 Allure 测试结果，跳过报告生成'
          }
        }
      }
    }

    stage('Send Email') {
      steps {
        echo "🔍 当前状态：${currentBuild.currentResult}"
        echo '📩 发送 HTML 格式邮件，嵌入状态图标和失败截图'
        script {
          def statusIcon = currentBuild.currentResult == 'SUCCESS'
            ? '✅ <span style="color:green;">成功</span>'
            : '❌ <span style="color:red;">失败</span>'

          catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
            emailext(
              subject: "🔔 自动化测试报告 - 构建 #${env.BUILD_NUMBER} [${currentBuild.currentResult}]",
              mimeType: 'text/html',
              to: "${env.RECIPIENTS}",
              attachmentsPattern: 'target/screenshots/*.png',
              attachLog: true,
              body: """
                <html>
                <body style="font-family:Arial;">
                  <h2 style="color:#333;">🔔 自动化测试结果通知</h2>
                  <p><b>构建状态：</b>${statusIcon}</p>
                  <p><b>项目：</b>${env.JOB_NAME}</p>
                  <p><b>构建编号：</b>#${env.BUILD_NUMBER}</p>
                  <p><b>构建时间：</b>${new Date().format("yyyy-MM-dd HH:mm:ss")}</p>
                  <p><b>📊 Allure 报告：</b><a href="${env.BUILD_URL}allure">点击查看报告</a></p>
                  <p><b>🔍 控制台输出：</b><a href="${env.BUILD_URL}console">点击查看日志</a></p>
                  <hr/>
                  <h3>📸 截图预览：</h3>
                  <p><i>下图为自动捕获的失败截图（如果有）</i></p>
                  <img src="cid:error-01.png" width="400" />
                  <img src="cid:error-02.png" width="400" />
                  <br/><br/>
                  <p style="font-size:12px;color:gray;">-- Jenkins 自动通知</p>
                </body>
                </html>
              """
            )
          }

          def screenshotExists = bat(script: 'if exist target\\screenshots\\*.png (exit 0) else (exit 1)', returnStatus: true) == 0
          if (screenshotExists) {
            catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
              archiveArtifacts artifacts: 'target/screenshots/*.png'
            }
          } else {
            echo 'ℹ️ 未发现截图文件，跳过归档'
          }
        }
      }
    }

    stage('Notify WeChat / DingTalk') {
      when {
        expression { return currentBuild.currentResult != 'ABORTED' }
      }
      steps {
        echo '📲 调用 Java 通知主程序'
        echo "🔍 当前状态：${currentBuild.currentResult}"
        echo "🔍 报告地址：http://localhost:8080/job/autoTest/allure/"
        catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
          bat 'mvn compile -Dfile.encoding=UTF-8'
          bat """
            java -cp "target/classes" ^
            -Dbuild.status=${currentBuild.currentResult} ^
            -Dreport.allure.link=http://localhost:8080/job/autoTest/allure/ ^
            com.baidu.notification.SendNotificationMain
          """
        }
      }
    }
  }

  post {
    always {
      echo "🧹 构建后操作：使用 Allure 报告展示"
      echo "✔️ 构建结束 ➤ Allure 报告地址：http://localhost:8080/job/autoTest/allure/"

      catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
        archiveArtifacts artifacts: 'allure-report/**', allowEmptyArchive: true
      }

      script {
        def screenshotsExist = fileExists('target/screenshots') &&
                               bat(script: 'dir target\\screenshots\\*.png >nul 2>&1', returnStatus: true) == 0

        if (screenshotsExist) {
          echo '📸 发现截图文件，准备归档'
          catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
            archiveArtifacts artifacts: 'target/screenshots/*.png'
          }
        } else {
          echo 'ℹ️ 构建后未发现截图，跳过归档'
        }
      }
    }

    success {
      echo "🎉 构建成功"
    }

    failure {
      echo "❌ 构建失败，请检查日志"
    }
  }
}
