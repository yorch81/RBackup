<!DOCTYPE html>
<html lang="en">
	<head>
		<title>RBackup Application</title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="RBackup Application">
		<meta name="author" content="Jorge Alberto Ponce Turrubiates">
		
    	<link rel="shortcut icon" type="image/x-icon" href="./img/favicon.ico">

		<style type="text/css">
			.body {
			 	min-height: 2000px;
			}

			.navbar-static-top {
			  	margin-bottom: 19px;
			}

			.navbar {
				min-height:110px;
				height:60px;
			}

			.bsnavbar {
			  	margin-bottom: 19px;
				min-height:110px;
			}
			
			.example {
				float: left;
				margin: 15px;
			}
			
			.file_explorer {
				width: 300px;
				height: 300px;
				border-top: solid 1px #BBB;
				border-left: solid 1px #BBB;
				border-bottom: solid 1px #FFF;
				border-right: solid 1px #FFF;
				background: #FFF;
				overflow: scroll;
				padding: 5px;
			}

			.modal-static { 
		        position: fixed;
		        top: 50% !important; 
		        left: 50% !important; 
		        margin-top: -100px;  
		        margin-left: -100px; 
		        overflow: visible !important;
		    }

		    .modal-static,
		    .modal-static .modal-dialog,
		    .modal-static .modal-content {
		        width: 200px; 
		        height: 200px; 
		    }

		    .modal-static .modal-dialog,
		    .modal-static .modal-content {
		        padding: 0 !important; 
		        margin: 0 !important;
		    }

		    .modal-static .modal-content .icon {
		    }
		</style>
		
		<script src="./js/jquery-1.9.1.js" type="text/javascript"></script>		
    	
		<script src="./jQueryFileTree-master/jqueryFileTree.js" type="text/javascript"></script>
		<link href="./jQueryFileTree-master/jqueryFileTree.css" rel="stylesheet" type="text/css" media="screen" />
		
		<link rel="stylesheet" href="./metro-bootstrap-master/dist/css/metro-bootstrap.min.css" />
		<script src="../bootstrap-3.2.0-dist/js/bootstrap.min.js"></script>
    	<script src="./js/bootbox.js"></script>

		<script type="text/javascript">	
			/**
			 * Execute Remote Backup
			 */
			function RBackup(){
				var currentDir = "";
				var fileName = "";
				var dbName = "";
				
				/**
				 * Sets Current Directory
				 * @param String pCurrentDir Current Directory
				 */
				this.setCurrentDir =  function(pCurrentDir){
					currentDir = pCurrentDir;
				}
				
				/**
				 * Sets Backup File name
				 * @param String pFileName Backup File Name
				 */
				this.setFileName = function(pFileName){
					fileName = pFileName;
				}
				
				/**
				 * Sets Database Name to Backup
				 * @param String pDbName Database Name
				 */
				this.setDbName = function(pDbName){
					dbName = pDbName;
				}
				
				/**
				 * Gets Current Directory
				 * @return String Current Directory
				 */
				this.getCurrentDir =  function(){
					return currentDir;
				}
				
				/**
				 * Gets Backup File name
				 * @return String Backup File name
				 */
				this.getFileName = function(){
					return fileName;
				}
				
				/**
				 * Gets Database Name to Backup
				 * @return String Database Name 
				 */
				this.getDbName = function(){
					return dbName;
				}
				
				/**
				 * Execute Backup
				 * @return void
				 */
				this.backup = function(){	
					if (fileName == ""){
						bootbox.alert("Must type Backup File Name");
						$('#txtFile').focus();
					}
					else{
						$('#processing-modal').modal('toggle');
						$('#label-process').html('Processing: ' + fileName);
						fileName = currentDir + fileName;
						
						$.post('/rbackup', {filename: fileName, dbname: dbName},
								function(response,status) {
			                    	$('#processing-modal').modal('hide');
			                        
			                        if (response.length > 0) {
			                        	bootbox.alert(response);
			                        }
			                        else
			                        	location.reload(true);
			                }).error(
			                    function(){
			                        console.log('Application not responding');
			                    }
			                );
					}
				}

				/**
				 * Execute Restore
				 * @return void
				 */
				this.restore= function(){	
					if ($('#txtDbRestore').val() == ""){
						bootbox.alert("Must type DataBase to Restore");
						$('#txtDbRestore').focus();
					}
					else{
						$('#processing-restore').modal('hide');
						$('#processing-modal').modal('toggle');
						$('#label-process').html('Restoring: ' + $('#txtDbRestore').val());
						fileName = currentDir + fileName;
						
						$.post('/restore', {filename: fileName, dbname: $('#txtDbRestore').val()},
								function(response,status) {
			                    	$('#processing-modal').modal('hide');

			                    	if (response.length > 0) {
			                        	bootbox.alert(response);
			                        }
			                        else
			                        	location.reload(true);
			                }).error(
			                    function(){
			                        console.log('Application not responding');
			                    }
			                );
					}
				}
			}
		
			// Init JQuery
			$(document).ready( function() {				
				var rbackup =  new RBackup();
				
				$('#explorer').fileTree({ root: './', script: '/getfiles', folderEvent: 'click', expandSpeed: 750, collapseSpeed: 750, multiFolder: false }, function(file) { 

					// Gets File Name
					file = file.substring(2);
					var files = file.split("/");
					var arrLen = files.length - 1;
					file = files[arrLen];

					$('#txtFile').val(file);
					//$('#btn_backup').html("Restore");
				});
			
				$('#explorer').on('filetreeexpand', 
			    		function (e, data){
							$('#txtPath').val(data.rel);
							//$('#btn_backup').html("Backup");
							$('#txtFile').val("");
			    });
				
				$("#btn_backup").click(function(){
					rbackup.setCurrentDir($('#txtPath').val());
	    			rbackup.setFileName($('#txtFile').val());
	    			rbackup.setDbName($('#cmbDb').val());
	    			
					rbackup.backup();
			    });

				$("#btn_validate").click(function(){
					if($('#txtFile').val() != '')
						$('#processing-restore').modal('toggle');
					else
						bootbox.alert("Please select a backup for restore");
			    });
			    
			    $("#btn_restore").click(function(){
					rbackup.setCurrentDir($('#txtPath').val());
	    			rbackup.setFileName($('#txtFile').val());
	    			
					rbackup.restore();
			    });

			    $("#btn_credits").click(function() {
                    $('#window-credits').modal('toggle');
                });
			});
		</script>

	</head>
	
	<body>
		<div class="navbar navbar-default navbar-static-top bsnavbar">
	      <div class="container">
	      	<div class="navbar-header">
	          <h2>RBackup <sup>with Restore</sup></h2>
	    	</div>

	    	<ul class="nav navbar-nav navbar-right">
              <li class="active" id="btn_credits"><a href="#">Credits<span class="sr-only">(current)</span></a></li>
            </ul>
	      </div>
	    </div>
		
		<div class="container">
			<div class="example">
				<label for="txtPath">Selected Path:</label>
				<input id="txtPath" type="text" class="form-control" placeholder="Path" name="txtPath" value = "${baseDir}" required disabled>
				
				<label for="cmbDb">DataBases:</label>
				<select id="cmbDb" class="form-control">
					${listDb}
				</select>
				
				<input id="txtFile" type="text" class="form-control" placeholder="Backup File Name" name="txtFile" required>
								
				<div id="explorer" class="file_explorer"></div>
				<br>
				<div>
					<center>
						<button id="btn_backup" class="btn btn-lg btn-info">Backup</button>
						<button id="btn_validate" class="btn btn-lg btn-success">Restore</button>
					</center>
				</div>
			</div>    
		</div>
		
		<!-- Static Modal Restore Data -->
		<div class="modal fade" id="processing-restore" role="dialog" aria-hidden="true">
		    <div class="modal-dialog">
		        <div class="modal-content">
		        	<div class="modal-header">
				        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				        <h4 class="modal-title">Restore DataBase</h4>
				     </div>

				    <div class="modal-body">
				    	<label for="txtDbRestore">DataBase:</label>
		        		<input id="txtDbRestore" type="text" class="form-control" placeholder="DataBase Restore" name="txtDbRestore" required>
						<br>
						<button id="btn_restore" class="btn btn-lg btn-primary btn-block">Restore</button>
				    </div>
		        </div>
		    </div>
		</div>

		<!-- Static Modal Credits -->
        <div class="modal fade" id="window-credits" role="dialog" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">Credits</h4>
                     </div>

                    <div class="modal-body">
                        <center>
                            <p><h3>Jorge Alberto Ponce Turrubiates</h3></p>
                            <p><h5><a href="mailto:the.yorch@gmail.com">the.yorch@gmail.com</a></h5></p>
                            <p><h5><a href="http://the-yorch.blogspot.mx/">Blog</a></h5></p>
                            <p><h5><a href="https://bitbucket.org/yorch81">BitBucket</a></h5></p>
                            <p><h5><a href="https://github.com/yorch81">GitHub</a></h5></p>
                            <p></p>
                        </center>
                    </div>
                </div>
            </div>
        </div>

		<!-- Static Modal Processing -->
		<div class="modal modal-static fade" id="processing-modal" role="dialog" aria-hidden="true">
		    <div class="modal-dialog">
		        <div class="modal-content">
		           <div class="modal-body">
		                <div class="text-center">
		                    <img src="./img/processing.gif" class="icon" />
		                    <h5 id="label-process">Processing... 
		                    </h5>
		                </div>
		            </div>
		        </div>
		    </div>
		</div>
	</body>
</html>