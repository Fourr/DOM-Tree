package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root=null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {

		Stack <TagNode> stck = new Stack <TagNode>();
		String first =sc.nextLine();
		String first1 = first.substring(1,first.length()-1);
		TagNode rt = new TagNode (first1 ,null,null);
		stck.push(rt);
		root = rt;
		TagNode ptr = root;
		String second =sc.nextLine();
		String second1 = second.substring(1,second.length()-1);
		stck.push(rt.firstChild = new TagNode (second1 ,null ,null));
		ptr = ptr.firstChild;
		boolean satisfied = false;
		while (sc.hasNextLine()){
			String line = sc.nextLine();

			if (line.indexOf("<") ==0 && line.indexOf("/") ==1){
				ptr  = stck.pop();
				satisfied = true;
			}
			else if (line.indexOf("<") ==-1){
				if(satisfied == true){
					ptr.sibling = new TagNode(line, null,null);
					ptr = ptr.sibling;
					satisfied =false; 
				}else {
					ptr.firstChild = new TagNode (line,null,null);
					satisfied = true;
					ptr=ptr.firstChild;
				}
			}
			else {
				if(satisfied == true){
					String line1 = line.substring(1,line.length()-1);
					stck.push(ptr.sibling = new TagNode(line1, null, null));
					ptr = ptr.sibling;
					satisfied = false;
				}
				else if (satisfied == false){
					String line1 = line.substring(1,line.length()-1);
					stck.push (ptr.firstChild =new TagNode (line1,null,null));
					ptr= ptr.firstChild;

				}

			}
		}
	}


	public void removeTag(String tag) {
		if((tag.equals("p") || tag.equals("em") || tag.equals("b"))){
			findPointer(root, tag);
		}
		if(tag.equals("ol") || tag.equals("ul")){
			
			findPointer2(root, tag);
		}
		
	}

	private void findPointer2(TagNode root, String tag) { // tag to be removed is <ol> or <ul>
		if(root == null){
			return;
		}
		if(root.tag.equals(tag) && root.firstChild != null) {
			root.tag = "p";
			TagNode ptr = null;
			for(ptr = root.firstChild; ptr.sibling != null; ptr = ptr.sibling) ptr.tag = "p"; 
			
			ptr.tag = "p";
			ptr.sibling = root.sibling;
			root.sibling = root.firstChild.sibling;
			root.firstChild = root.firstChild.firstChild;
		}
		findPointer2(root.firstChild, tag); 
		findPointer2(root.sibling, tag);
	}

	private void findPointer (TagNode root, String tag){
		if(root == null) return;
		if(root.tag.equals(tag) && root.firstChild != null) {
			root.tag = root.firstChild.tag;
			if(root.firstChild.sibling != null) {
				TagNode ptr = null;
				for(ptr = root.firstChild; ptr.sibling != null; ptr = ptr.sibling); 
				ptr.sibling = root.sibling;
				root.sibling = root.firstChild.sibling;
			}
			root.firstChild = root.firstChild.firstChild;
		}
		findPointer(root.firstChild, tag); 
		findPointer(root.sibling, tag);
	}

	public void replaceTag(String oldTag, String newTag) {

		TagNode ptr = root;
		if(oldTag.equals(newTag)){
			return;
		}
		if (oldTag.equals("ol") && newTag.equals("ul")){
			helperMethod(oldTag, newTag, ptr);
			return;
		}else if (oldTag.equals("ul") && newTag.equals("ol")){
			helperMethod(oldTag, newTag, ptr);
			return;
		}else if (oldTag.equals("b") && newTag.equals("em")){
			helperMethod(oldTag, newTag, ptr);
			return;
		}else if (oldTag.equals("em") && newTag.equals("b")){
			helperMethod(oldTag, newTag, ptr);
			return;
		}else {
			return;
		}


	}

	private void helperMethod(String oldTag, String newTag, TagNode root) {
		if(root == null){
			return;
		}
		if(root.tag.equals(oldTag)){
			root.tag=newTag;
		}
	
		helperMethod(oldTag, newTag, root.firstChild);
	
		helperMethod(oldTag, newTag, root.sibling);
	
		return;
	}

	public void boldRow(int row) { 


		TagNode tr = traverse(root);
		if(traverse(root)==null){
			return;
		}
		for(int r=1; r < row; r++) {
			if(tr.sibling == null){
				return;
			}
			tr = tr.sibling;
		}
		for(TagNode td = tr.firstChild; td != null; td = td.sibling) { 
			TagNode b = new TagNode("b", td.firstChild, null);
			td.firstChild = b;
		}
	}


	private TagNode traverse(TagNode root) { 
		if(root == null){
			return null; 
		}
		if(root.tag.equals("tr")){
			return root; 
		}
		TagNode x = traverse(root.sibling);
		TagNode y = traverse(root.firstChild);
		if(x != null){
			return x; 
		}
		if(y != null){
			return y; 
		}

		return null;
	}


	private void bust(TagNode root, String word, String tag){
		if(root == null)
			return;
		bust(root.firstChild, word, tag);
		if(root.firstChild != null && root.firstChild.tag.contains(word)){
			String[] words = root.firstChild.tag.split(word);
			if(words.length == 2){
				TagNode rightside = new TagNode(words[1], null, root.firstChild.sibling);
				TagNode leftside = new TagNode(words[0], null , null);
				TagNode tagged = new TagNode(word, null, null);
				TagNode tagger = new TagNode(tag, tagged, rightside);
				root.firstChild = leftside;
				leftside.sibling = tagger;
				tagger.sibling = rightside;
				if(words[0].equals(""))
					root.firstChild = tagger;
			}
			else if(words.length == 0){
				TagNode tagger = new TagNode(tag, root.firstChild, root.sibling);
				root.firstChild = tagger;
				System.out.println("length 0");
			}
			else{	
				if(words[0].charAt(0)==' '){
					TagNode tagged = new TagNode(word, null, null);
					TagNode tagger = new TagNode(tag, tagged, null);
					TagNode right = new TagNode(words[0], null, root.firstChild.sibling);
					tagger.sibling = right;
					root.firstChild = tagger;
					
				} 
				else{
					TagNode tagged = new TagNode(word, null, null);
					TagNode tagger = new TagNode(tag, tagged, root.firstChild.sibling);
					TagNode left = new TagNode(words[0], null, tagger);
					root.firstChild = left;
				}
			}
			
		}
		if(root.sibling != null && root.sibling.tag.contains(word)){
			String[] words = root.sibling.tag.split(word);
			if(words.length > 0){
				TagNode rightside = new TagNode(words[1], null, root.sibling.sibling);
				TagNode leftside = new TagNode(words[0], null, null);
				TagNode tagged = new TagNode(word, null, null);
				TagNode tagger = new TagNode(tag, tagged, rightside);
				root.sibling = leftside;
				leftside.sibling = tagger;
				tagger.sibling = rightside;
			}
			else if(words.length == 0){
				TagNode tagger = new TagNode(tag, root.sibling, root.sibling.sibling);
				root.sibling = tagger;
				System.out.println("length 0");
			}
			else{
				TagNode tagged = new TagNode(word, null, null);
				TagNode tagger = new TagNode(tag, tagged, null);
				if(words[0].charAt(0) == ' '){
					TagNode right = new TagNode(words[0], null, root.sibling.sibling);
					tagger.sibling = right;
					root.sibling = tagger;
				}
				else{
					TagNode left = new TagNode(words[0], null, tagger);
					root.sibling = left;
				}
			}
		}
		bust(root.sibling, word, tag);
	}
	
	
	
	public void addTag(String word, String tag) {
		bust(root, word, tag);
	}

	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}

}
