<!DOCTYPE html>
<html lang="en">
	<head>
		<title>RBackup</title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		
		<style type="text/css">
			.body {
			 	min-height: 2000px;
			}

			.navbar-static-top {
			  	margin-bottom: 19px;
			}

			.navbar {
				min-height:124px;
			}

			.bsnavbar {
			  	margin-bottom: 19px;
				min-height:124px;
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
		
		<link rel="stylesheet" href="./bootstrap-3.3.1-dist/dist/css/bootstrap.min.css" />
		<script src="./bootstrap-3.3.1-dist/dist/js/bootstrap.min.js"></script>
		
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
						alert("Must type Backup File Name");
						$('#txtFile').focus();
					}
					else{
						$('#processing-modal').modal('toggle');
						$('#label-process').html('Processing: ' + fileName);
						
						$.post('/rbackup', {currentdir: currentDir, filename: fileName, dbname: dbName},
								function(response,status) {
			                    	$('#processing-modal').modal('hide');
			                        		                        
			                        if (response == '1') {
			                        	alert("File Already Exists");
			                        } else if (response == '2') {
			                        	alert("Not Connected to SQL Server");
			                        } else if (response == '3') {
			                        	alert("DataBase Server Exception");
			                        }
				                        
			                        location.reload(true)
			                }).error(
			                    function(){
			                        console.log('Application not responding');
			                    }
			                );
					}
				}
			}
		
			$(document).ready( function() {				
				var rbackup =  new RBackup();
				
				$('#explorer').fileTree({ root: './', script: '/getfiles', folderEvent: 'click', expandSpeed: 750, collapseSpeed: 750, multiFolder: false }, function(file) { 
					file = file.substring(2);
				});
			
				$('#explorer').on('filetreeexpand', 
			    		function (e, data){
							$('#txtPath').val(data.rel);
			    });
				
				$("#btn_backup").click(function(){
					rbackup.setCurrentDir($('#txtPath').val());
	    			rbackup.setFileName($('#txtFile').val());
	    			rbackup.setDbName($('#cmbDb').val());
	    			
					rbackup.backup();
			     });
			});
		</script>

	</head>
	
	<body>
		<div class="navbar navbar-default navbar-static-top bsnavbar">
	      <div class="container">
	      	<div class="navbar-header">
	          <h1>RBackup</h1>
	    	</div>
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
				<button id="btn_backup" class="btn btn-lg btn-primary btn-block">Backup</button>
				
				<div id="explorer" class="file_explorer"></div>
			</div>
		</div>
		
		<!-- Static Modal -->
		<div class="modal modal-static fade" id="processing-modal" role="dialog" aria-hidden="true">
		    <div class="modal-dialog">
		        <div class="modal-content">
		           <div class="modal-body">
		                <div class="text-center">
		                    <img src="./img/procesando.gif" class="icon" />
		                    <h5 id="label-process">Processing... 
		                    </h5>
		                </div>
		            </div>
		        </div>
		    </div>
		</div>

	</body>
</html>